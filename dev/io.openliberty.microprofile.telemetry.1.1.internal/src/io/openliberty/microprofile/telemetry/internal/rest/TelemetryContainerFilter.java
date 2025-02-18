/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.microprofile.telemetry.internal.rest;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import io.openliberty.microprofile.telemetry.internal.cdi.OpenTelemetryInfo;
import io.openliberty.microprofile.telemetry.internal.helper.AgentDetection;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanStatusExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.net.NetServerAttributesGetter;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import io.openliberty.microprofile.telemetry.common.rest.RestRouteCache;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TelemetryContainerFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String INSTRUMENTATION_NAME = "io.openliberty.microprofile.telemetry";

    private static final String REST_RESOURCE_CLASS = "rest.resource.class";
    private static final String REST_RESOURCE_METHOD = "rest.resource.method";

    private static final String SPAN_CONTEXT = "otel.span.server.context";
    private static final String SPAN_PARENT_CONTEXT = "otel.span.server.parentContext";
    static final String SPAN_SCOPE = "otel.span.server.scope";

    private static final HttpServerAttributesGetterImpl HTTP_SERVER_ATTRIBUTES_GETTER = new HttpServerAttributesGetterImpl();

    private static final RestRouteCache ROUTE_CACHE = new RestRouteCache();

    private Instrumenter<ContainerRequestContext, ContainerResponseContext> instrumenter;

    @jakarta.ws.rs.core.Context
    private ResourceInfo resourceInfo;

    // RESTEasy requires no-arg constructor for CDI injection: https://issues.redhat.com/browse/RESTEASY-1538
    public TelemetryContainerFilter() {
    }

    @Inject
    public TelemetryContainerFilter(final OpenTelemetryInfo openTelemetry) {
        if (openTelemetry.getEnabled() && !AgentDetection.isAgentActive()) {
            InstrumenterBuilder<ContainerRequestContext, ContainerResponseContext> builder = Instrumenter.builder(
                                                                                                                  openTelemetry.getOpenTelemetry(),
                                                                                                                  INSTRUMENTATION_NAME,
                                                                                                                  HttpSpanNameExtractor.create(HTTP_SERVER_ATTRIBUTES_GETTER));

            this.instrumenter = builder
                            .setSpanStatusExtractor(HttpSpanStatusExtractor.create(HTTP_SERVER_ATTRIBUTES_GETTER))
                            .addAttributesExtractor(HttpServerAttributesExtractor.create(HTTP_SERVER_ATTRIBUTES_GETTER))
                            .buildServerInstrumenter(new ContainerRequestContextTextMapGetter());

        } else {
            this.instrumenter = null;
        }
    }

    @Override
    public void filter(final ContainerRequestContext request) {
        if (instrumenter == null) {
            return;
        }

        Context parentContext = Context.current();
        if (instrumenter.shouldStart(parentContext, request)) {
            request.setProperty(REST_RESOURCE_CLASS, resourceInfo.getResourceClass());
            request.setProperty(REST_RESOURCE_METHOD, resourceInfo.getResourceMethod());

            Context spanContext = instrumenter.start(parentContext, request);
            Scope scope = spanContext.makeCurrent();
            request.setProperty(SPAN_CONTEXT, spanContext);
            request.setProperty(SPAN_PARENT_CONTEXT, parentContext);
            request.setProperty(SPAN_SCOPE, scope);
        }
    }

    @Override
    public void filter(final ContainerRequestContext request, final ContainerResponseContext response) {
        // Note: for async resource methods, this may not run on the same thread as the other filter method
        // Scope is ended in TelemetryServletRequestListener to ensure it does run on the original request thread

        if (instrumenter == null) {
            return;
        }

        Context spanContext = (Context) request.getProperty(SPAN_CONTEXT);
        if (spanContext == null) {
            return;
        }

        try {
            instrumenter.end(spanContext, request, response, null);
        } finally {
            request.removeProperty(REST_RESOURCE_CLASS);
            request.removeProperty(REST_RESOURCE_METHOD);
            request.removeProperty(SPAN_CONTEXT);
            request.removeProperty(SPAN_PARENT_CONTEXT);
        }
    }

    private static class ContainerRequestContextTextMapGetter implements TextMapGetter<ContainerRequestContext> {

        @Override
        public Iterable<String> keys(final ContainerRequestContext carrier) {
            return carrier.getHeaders().keySet();
        }

        @Override
        public String get(final ContainerRequestContext carrier, final String key) {
            if (carrier == null) {
                return null;
            }

            return carrier.getHeaders().getOrDefault(key, singletonList(null)).get(0);
        }
    }

    private static class HttpServerAttributesGetterImpl implements HttpServerAttributesGetter<ContainerRequestContext, ContainerResponseContext> {

        @Override
        public String getHttpRoute(final ContainerRequestContext request) {

            Class<?> resourceClass = (Class<?>) request.getProperty(REST_RESOURCE_CLASS);
            Method resourceMethod = (Method) request.getProperty(REST_RESOURCE_METHOD);

            String route = ROUTE_CACHE.getRoute(resourceClass, resourceMethod);

            if (route == null) {

                String contextRoot = request.getUriInfo().getBaseUri().getPath();
                UriBuilder template = UriBuilder.fromPath(contextRoot);

                if (resourceClass.isAnnotationPresent(Path.class)) {
                    template.path(resourceClass);
                }

                if (resourceMethod.isAnnotationPresent(Path.class)) {
                    template.path(resourceMethod);
                }

                route = template.toTemplate();
                ROUTE_CACHE.putRoute(resourceClass, resourceMethod, route);
            }

            return route;
        }

        //required
        @Override
        public String getHttpRequestMethod(final ContainerRequestContext request) {
            return request.getMethod();
        }

        @Override
        public String getUrlPath(final ContainerRequestContext request) {
            URI requestUri = request.getUriInfo().getRequestUri();
            String path = requestUri.getPath();
            return path;
        }

        @Override
        public String getUrlQuery(final ContainerRequestContext request) {
            URI requestUri = request.getUriInfo().getRequestUri();
            return requestUri.getQuery();
        }

        @Override
        public String getUrlScheme(final ContainerRequestContext request) {
            return request.getUriInfo().getRequestUri().getScheme();
        }

        //If one was sent
        @Override
        public Integer getHttpResponseStatusCode(final ContainerRequestContext request, final ContainerResponseContext response, @Nullable Throwable error) {
            return response.getStatus();
        }

        @Override
        public List<String> getHttpRequestHeader(final ContainerRequestContext request, final String name) {
            return request.getHeaders().getOrDefault(name, emptyList());
        }

        @Override
        public List<String> getHttpResponseHeader(final ContainerRequestContext request, final ContainerResponseContext response,
                                                  final String name) {
            return response.getStringHeaders().getOrDefault(name, emptyList());
        }

                @Override
        public String getTransport(ContainerRequestContext request) {
            return SemanticAttributes.NetTransportValues.IP_TCP;
        }

        @Override
        public String getServerAddress(ContainerRequestContext request) {
            return request.getUriInfo().getRequestUri().getHost();
        }

        @Override
        public Integer getServerPort(ContainerRequestContext request) {
            return request.getUriInfo().getRequestUri().getPort();
        }
    }
}