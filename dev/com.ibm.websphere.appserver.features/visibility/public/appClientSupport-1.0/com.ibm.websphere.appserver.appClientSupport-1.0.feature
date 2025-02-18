-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.appClientSupport-1.0
WLP-DisableAllFeatures-OnConflict: false
visibility=public
IBM-ShortName: appClientSupport-1.0
Subsystem-Name: Application Client Support for Server 1.0
-features=\
  com.ibm.websphere.appserver.eeCompatible-6.0; ibm.tolerates:="7.0, 8.0", \
  com.ibm.websphere.appclient.appClient-1.0, \
  com.ibm.websphere.appserver.injection-1.0, \
  com.ibm.websphere.appserver.clientContainerRemoteSupport-1.0
kind=ga
edition=base
WLP-Activation-Type: parallel
