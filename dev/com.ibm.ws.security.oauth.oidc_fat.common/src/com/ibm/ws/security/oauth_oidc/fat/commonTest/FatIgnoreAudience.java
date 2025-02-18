/*******************************************************************************
 * Copyright (c) 2010, 2020 IBM Corporation and others.
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


package com.ibm.ws.security.oauth_oidc.fat.commonTest;

import com.google.gson.JsonObject;

import java.security.SignatureException;
import net.oauth.jsontoken.Checker;

/**
 * Allows any audience (even null).
 */
public class FatIgnoreAudience implements Checker {

    @Override
    public void check(JsonObject payload) throws SignatureException {
        // don't throw - allow anything
    }
}
