package com.opcuatest.demo;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

import java.io.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static com.opcuatest.demo.utils.KeyLoader.loadPrivateKeyFromResources;

public class MyOpcUaServer {

    public static void main(String[] args) throws ExecutionException, InterruptedException, CertificateException, IOException {

        // Load the server certificate and private key from files

        // wczytywanie certyfikatu z pliku server_cert.pem
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream certFile = new FileInputStream("src/main/resources/security/server_cert.pem");
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(certFile);

        // wczytywanie klucza prywatnego z pliku server_key.pem
        PrivateKey privateKey = loadPrivateKeyFromResources("security/server_key.pem");
        KeyPair keyPair = new KeyPair(certificate.getPublicKey(), privateKey);

        // Create an instance of the X509IdentityValidator
        Predicate<X509Certificate> certificatePredicate = cert -> true;
        X509IdentityValidator identityValidator = new X509IdentityValidator(certificatePredicate);

        EndpointConfiguration endpoint = EndpointConfiguration.newBuilder()
                .setBindAddress("0.0.0.0")
                .setBindPort(4840)
                .setPath("/opcua/example")
                .build();

        // Create the server configuration
        OpcUaServerConfig serverConfig = OpcUaServerConfig.builder()
                .setApplicationName(LocalizedText.english("My OPC UA Server"))
                .setApplicationUri("urn:localhost:MyServer")
                .setBuildInfo(new BuildInfo("MyServer", "v1.0.0", "My OPC UA Server", null, null, null))
                .setCertificateManager(new DefaultCertificateManager(keyPair, certificate))
                .setIdentityValidator(identityValidator)
                .setProductUri("urn:mycompany:myproduct")
                .setEndpoints(Set.of(endpoint))
                .build();

        // Create the OPC UA server instance
        OpcUaServer server = new OpcUaServer(serverConfig);

        //TODO: Add a Username/Password authentication source
//      server.getAuthenticationManager().setIdentityValidator(new UsernameIdentityValidator(
//                true, authenticationChallenge -> {
//            String user = authenticationChallenge.getUsername();
//            String password = authenticationChallenge.getPassword();
//
//            if ("myuser".equals(user) && "mypassword".equals(password)) {
//                return CompletableFuture.completedFuture(
//                        new UserIdentity(new AnonymousIdentityToken("1")));
//            }
//
//            return CompletableFuture.failedFuture(new BadUserAccessDeniedException(
//                    "Invalid username or password"));
//        }));

        // Start the server
        server.startup();
        System.out.println("OPC UA server is now running.");
        Thread.sleep(1000000);
    }
}
