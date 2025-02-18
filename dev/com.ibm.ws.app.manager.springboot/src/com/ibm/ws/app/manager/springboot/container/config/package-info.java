/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
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
/**
 * Provides the classes necessary to implement a &quot;work-stealing&quot; threading
 * implementation.
 */
@Version("1.0.0")
@TraceOptions(traceGroups = { "springboot", "applications", "app.manager" }, messageBundle = "com.ibm.ws.app.manager.springboot.internal.resources.Messages")
package com.ibm.ws.app.manager.springboot.container.config;

import org.osgi.annotation.versioning.Version;

import com.ibm.websphere.ras.annotation.TraceOptions;
