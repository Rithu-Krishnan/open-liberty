/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
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
package org.apache.bval.jsr303;

/**
 * This is a no-op extender so that we maintain compatibility with bean validation
 * 1.0 apps that explicitly listed the apache message interpolator class
 * in the <message-interpolator> element in validation.xml.
 * 
 * <p>
 * Apache Bean Validation changed the package name in their 1.1 implementation,
 * so we provide this class with the original package name to allow 1.0 applications
 * to move up without needing to change their application.
 */
public class DefaultMessageInterpolator extends org.apache.bval.jsr.DefaultMessageInterpolator {

}
