/*******************************************************************************
 * Copyright (c) 2021,2022 IBM Corporation and others.
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
package io.openliberty.concurrent.internal.processor;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.dd.common.ManagedExecutor;
import com.ibm.ws.javaee.dd.common.Property;
import com.ibm.wsspi.injectionengine.ComponentNameSpaceConfiguration;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionConfigurationException;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.JNDIEnvironmentRefType;

import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedExecutorService;

/**
 * Injection binding for ManagedExecutorDefinition annotation
 * and managed-executor deployment descriptor element.
 */
public class ManagedExecutorDefinitionBinding extends InjectionBinding<ManagedExecutorDefinition> {
    private static final TraceComponent tc = Tr.register(ManagedExecutorDefinitionBinding.class);

    private static final String KEY_CONTEXT = "context";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_HUNG_TASK_THRESHOLD = "hungTaskThreshold";
    private static final String KEY_MAX_ASYNC = "maxAsync";

    private String contextServiceJndiName;
    private boolean XMLContextServiceRef;

    private String description;
    private boolean XMLDescription;

    private Long hungTaskThreshold;
    private boolean XMLHungTaskThreshold;

    private Integer maxAsync;
    private boolean XMLMaxAsync;

    private Map<String, String> properties;
    private final Set<String> XMLProperties = new HashSet<String>();

    public ManagedExecutorDefinitionBinding(String jndiName, ComponentNameSpaceConfiguration nameSpaceConfig) {
        super(null, nameSpaceConfig);
        setJndiName(jndiName);
    }

    @Override
    public Class<?> getAnnotationType() {
        return ManagedExecutorDefinition.class;
    }

    @Override
    protected JNDIEnvironmentRefType getJNDIEnvironmentRefType() {
        return JNDIEnvironmentRefType.ManagedExecutor;
    }

    @Override
    public void merge(ManagedExecutorDefinition annotation, Class<?> instanceClass, Member member) throws InjectionException {
        final boolean trace = TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled();
        if (trace)
            Tr.entry(this, tc, "merge", toString(annotation), instanceClass, member,
                     (XMLContextServiceRef ? "(xml)" : "     ") + "contextServiceRef: " + contextServiceJndiName + " << " + annotation.context(),
                     (XMLHungTaskThreshold ? "(xml)" : "     ") + "hungTaskThreshold: " + hungTaskThreshold + " << " + annotation.hungTaskThreshold(),
                     (XMLMaxAsync ? "         (xml)" : "              ") + "maxAsync: " + maxAsync + " << " + annotation.maxAsync());

        if (member != null) {
            // ManagedExecutorDefinition is a class-level annotation only.
            throw new IllegalArgumentException(member.toString());
        }

        contextServiceJndiName = mergeAnnotationValue(contextServiceJndiName, XMLContextServiceRef, annotation.context(), KEY_CONTEXT, "java:comp/DefaultContextService");
        description = mergeAnnotationValue(description, XMLDescription, "", KEY_DESCRIPTION, ""); // ManagedExecutorDefinition has no description attribute
        hungTaskThreshold = mergeAnnotationValue(hungTaskThreshold, XMLHungTaskThreshold, annotation.hungTaskThreshold(), KEY_HUNG_TASK_THRESHOLD, -1L);
        maxAsync = mergeAnnotationValue(maxAsync, XMLMaxAsync, annotation.maxAsync(), KEY_MAX_ASYNC, -1);
        properties = mergeAnnotationProperties(properties, XMLProperties, new String[] {}); // ManagedExecutorDefinition has no properties attribute

        if (trace)
            Tr.exit(this, tc, "merge", new String[] {
                                                      (XMLContextServiceRef ? "(xml)" : "     ") + "contextServiceRef= " + contextServiceJndiName,
                                                      (XMLHungTaskThreshold ? "(xml)" : "     ") + "hungTaskThreshold= " + hungTaskThreshold,
                                                      (XMLMaxAsync ? "         (xml)" : "              ") + "maxAsync= " + maxAsync
            });
    }

    void mergeXML(ManagedExecutor mxd) throws InjectionConfigurationException {
        final boolean trace = TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled();
        if (trace)
            Tr.entry(this, tc, "mergeXML", mxd, mxd.getName(),
                     (XMLContextServiceRef ? "(xml)" : "     ") + "contextServiceRef: " + contextServiceJndiName + " << " + mxd.getContextServiceRef(),
                     (XMLHungTaskThreshold ? "(xml)" : "     ") + "hungTaskThreshold: " + hungTaskThreshold + " << " + mxd.getHungTaskThreshold(),
                     (XMLMaxAsync ? "         (xml)" : "              ") + "maxAsync: " + maxAsync + " << " + mxd.getMaxAsync());

        List<Description> descriptionList = mxd.getDescriptions();

        String contextServiceRefValue = mxd.getContextServiceRef();
        if (contextServiceRefValue != null) {
            contextServiceJndiName = mergeXMLValue(contextServiceJndiName, contextServiceRefValue, "context-service-ref", KEY_CONTEXT, null);
            XMLContextServiceRef = true;
        }

        if (description != null) {
            description = mergeXMLValue(description, descriptionList.toString(), "description", KEY_DESCRIPTION, null);
            XMLDescription = true;
        }

        if (mxd.isSetHungTaskThreshold()) {
            hungTaskThreshold = mergeXMLValue(hungTaskThreshold, mxd.getHungTaskThreshold(), "hung-task-threshold", KEY_HUNG_TASK_THRESHOLD, null);
            XMLHungTaskThreshold = true;
        }

        if (mxd.isSetMaxAsync()) {
            maxAsync = mergeXMLValue(maxAsync, mxd.getMaxAsync(), "max-async", KEY_MAX_ASYNC, null);
            XMLMaxAsync = true;
        }

        List<Property> mxdProps = mxd.getProperties();
        properties = mergeXMLProperties(properties, XMLProperties, mxdProps);

        if (trace)
            Tr.exit(this, tc, "mergeXML", new String[] {
                                                         (XMLContextServiceRef ? "(xml)" : "     ") + "contextServiceRef= " + contextServiceJndiName,
                                                         (XMLHungTaskThreshold ? "(xml)" : "     ") + "hungTaskThreshold= " + hungTaskThreshold,
                                                         (XMLMaxAsync ? "         (xml)" : "              ") + "maxAsync= " + maxAsync
            });
    }

    @Override
    public void mergeSaved(InjectionBinding<ManagedExecutorDefinition> injectionBinding) throws InjectionException {
        ManagedExecutorDefinitionBinding managedExecutorBinding = (ManagedExecutorDefinitionBinding) injectionBinding;

        mergeSavedValue(contextServiceJndiName, managedExecutorBinding.contextServiceJndiName, "context-service-ref");
        mergeSavedValue(description, managedExecutorBinding.description, "description");
        mergeSavedValue(hungTaskThreshold, managedExecutorBinding.hungTaskThreshold, "hung-task-threshold");
        mergeSavedValue(maxAsync, managedExecutorBinding.maxAsync, "max-async");
        mergeSavedValue(properties, managedExecutorBinding.properties, "properties");
    }

    void resolve() throws InjectionException {
        Map<String, Object> props = new HashMap<String, Object>();

        if (properties != null) {
            props.putAll(properties);
        }

        // Insert all remaining attributes.
        addOrRemoveProperty(props, KEY_CONTEXT, contextServiceJndiName);
        addOrRemoveProperty(props, KEY_DESCRIPTION, description);
        addOrRemoveProperty(props, KEY_HUNG_TASK_THRESHOLD, hungTaskThreshold);
        addOrRemoveProperty(props, KEY_MAX_ASYNC, maxAsync);

        setObjects(null, createDefinitionReference(null, ManagedExecutorService.class.getName(), props));
    }

    @Trivial
    static final String toString(ManagedExecutorDefinition anno) {
        StringBuilder b = new StringBuilder();
        b.append("ManagedExecutorDefinition@").append(Integer.toHexString(anno.hashCode())) //
                        .append("(name=").append(anno.name()) //
                        .append(", context=").append(anno.context()) //
                        .append(", hungTaskThreshold=").append(anno.hungTaskThreshold()) //
                        .append(", maxAsync=").append(anno.maxAsync()) //
                        .append(")");
        return b.toString();
    }
}
