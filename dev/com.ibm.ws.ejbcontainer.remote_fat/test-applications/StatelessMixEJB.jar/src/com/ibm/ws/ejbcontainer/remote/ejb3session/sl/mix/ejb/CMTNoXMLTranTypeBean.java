/*******************************************************************************
 * Copyright (c) 2006, 2018 IBM Corporation and others.
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
package com.ibm.ws.ejbcontainer.remote.ejb3session.sl.mix.ejb;

import static javax.ejb.TransactionManagementType.CONTAINER;

import java.util.logging.Logger;

import javax.ejb.TransactionManagement;

import com.ibm.websphere.ejbcontainer.test.tools.FATTransactionHelper;

/**
 * Bean implementation class for Enterprise Bean: CMTNoXMLTranTypeBean
 **/
@TransactionManagement(CONTAINER)
public class CMTNoXMLTranTypeBean {
    private static final String CLASS_NAME = CompCMTStatelessRemoteBean.class.getName();
    private final static Logger svLogger = Logger.getLogger(CLASS_NAME);

    /**
     * Used to verify when a method with a REQUIRES_NEW transaction attribute is
     * called while calling thread is currently associated with a global
     * transaction causes the container to dispatch the method in the a new
     * global transaction context (e.g container does begin a new global
     * transaction). The caller must begin a global transaction prior to calling
     * this method.
     *
     * @param tid
     *            is the global transaction ID for the transaction that was
     *            started prior to calling this method.
     *
     * @return boolean true if method is dispatched in a global transaction with
     *         a global transaction ID that does not match the tid parameter.
     *         Otherwise boolean false is returned.
     *
     * @throws java.lang.IllegalStateException
     *             is thrown if method is dispatched while not in any
     *             transaction context.
     */
    public boolean txRequiresNew(byte[] tid) {
        byte[] myTid = FATTransactionHelper.getTransactionId();
        if (myTid == null) {
            svLogger.info("*** myTid == null: This is not expected.");
            return false;
        }

        return (FATTransactionHelper.isSameTransactionId(tid) == false);
    }

    /** Required default constructor **/
    public CMTNoXMLTranTypeBean() {}
}