-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.io.netty.ssl
WLP-DisableAllFeatures-OnConflict: false
singleton=true
-bundles=io.openliberty.io.netty.ssl; location:="lib/";
kind=noship
edition=full
WLP-Activation-Type: parallel
