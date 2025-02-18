-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.mdb-4.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-ShortName: mdb-4.0
IBM-API-Package: com.ibm.ws.ejbcontainer.mdb; type="internal"
Subsystem-Category: JakartaEE9Application
-features=com.ibm.websphere.appserver.eeCompatible-9.0; ibm.tolerates:="10.0, 11.0", \
  io.openliberty.connectors-2.0; ibm.tolerates:="2.1", \
  io.openliberty.ejbCore-2.0, \
  io.openliberty.jakarta.interceptor-2.0; ibm.tolerates:="2.1, 2.2", \
  io.openliberty.jakarta.enterpriseBeans-4.0, \
  com.ibm.websphere.appserver.transaction-2.0
-bundles=com.ibm.ws.ejbcontainer.mdb.jakarta, \
 io.openliberty.ejbcontainer.v40.internal
Subsystem-Name: Jakarta Enterprise Beans 4.0 Message-Driven Beans
kind=ga
edition=base
WLP-Activation-Type: parallel
