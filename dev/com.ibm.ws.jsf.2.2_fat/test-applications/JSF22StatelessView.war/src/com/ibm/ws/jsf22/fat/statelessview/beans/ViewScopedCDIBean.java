/*******************************************************************************
 * Copyright (c) 2015, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.ibm.ws.jsf22.fat.statelessview.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * A simple CDI bean that will be used to test
 * a view scoped CDI bean.
 *
 * @author Bill Lucy
 *
 */
@Named
@ViewScoped
public class ViewScopedCDIBean implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    private Timestamp timestamp;

    public ViewScopedCDIBean() {
        Date date = new Date();
        timestamp = new Timestamp(date.getTime());
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
