/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation and others.
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

package com.ibm.ws.jpa.tests.spec10.injection.mdb;

import java.io.File;

import componenttest.common.apiservices.Bootstrap;
import componenttest.rules.repeater.RepeatTestAction;

/**
 *
 */
public class RepeatWithJPA20 implements RepeatTestAction {
    public static final String ID = "JPA20";

    @Override
    public boolean isEnabled() {
        try {
            Bootstrap b = Bootstrap.getInstance();
            String installRoot = b.getValue("libertyInstallPath");
            File jpa20Feature = new File(installRoot + "/lib/features/com.ibm.websphere.appserver.jpa-2.0.mf");
            return jpa20Feature.exists();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Set JPA feature to 2.0 version";
    }

    @Override
    public void setup() throws Exception {
        RepeaterInfo.repeatPhase = "jpa20.xml";
    }

    @Override
    public String getID() {
        return ID;
    }
}
