/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
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

package fats.cxf.basicssl.wssec;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.6.2
 * 2012-11-09T22:47:08.554-06:00
 * Generated source version: 2.6.2
 * 
 */
@WebServiceClient(name = "FVTVersionBA7Service", 
                  wsdlLocation = "BasicPlcyBA.wsdl",
                  targetNamespace = "http://wssec.basicssl.cxf.fats") 
public class FVTVersionBA7Service extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://wssec.basicssl.cxf.fats", "FVTVersionBA7Service");
    public final static QName UrnBasicPlcyBA7 = new QName("http://wssec.basicssl.cxf.fats", "UrnBasicPlcyBA7");
    static {
        URL url = FVTVersionBA7Service.class.getResource("BasicPlcyBA.wsdl");
        if (url == null) {
            java.util.logging.Logger.getLogger(FVTVersionBA7Service.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "BasicPlcyBA.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public FVTVersionBA7Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public FVTVersionBA7Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FVTVersionBA7Service() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     *
     * @return
     *     returns FVTVersionBA
     */
    @WebEndpoint(name = "UrnBasicPlcyBA7")
    public FVTVersionBA getUrnBasicPlcyBA7() {
        return super.getPort(UrnBasicPlcyBA7, FVTVersionBA.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns FVTVersionBA
     */
    @WebEndpoint(name = "UrnBasicPlcyBA7")
    public FVTVersionBA getUrnBasicPlcyBA7(WebServiceFeature... features) {
        return super.getPort(UrnBasicPlcyBA7, FVTVersionBA.class, features);
    }

}
