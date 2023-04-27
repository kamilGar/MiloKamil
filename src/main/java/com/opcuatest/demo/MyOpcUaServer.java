package com.opcuatest.demo;

import com.google.common.collect.Lists;
import org.eclipse.milo.opcua.sdk.client.api.identity.X509IdentityProvider;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.transport.TransportProfile;
import org.eclipse.milo.opcua.stack.core.types.structured.AnonymousIdentityToken;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.core.types.structured.UserTokenPolicy;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.opcuatest.demo.KeyLoader.loadPrivateKeyFromResources;

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

        // Create an instance of the X509IdentityProvider
        X509IdentityProvider identityProvider = new X509IdentityProvider(certificate, privateKey);

        // Create the server configuration
        OpcUaServerConfig serverConfig = OpcUaServerConfig.builder()
                .setApplicationName(LocalizedText.english("My OPC UA Server"))
                .setApplicationUri("urn:localhost:MyServer")
                .setBuildInfo(new BuildInfo("MyServer", "v1.0.0", "My OPC UA Server", null, null, null))
                .setCertificateManager(new DefaultCertificateManager(keyPair, certificate))
                .setProductUri("urn:mycompany:myproduct")
                .build();
//                .setEndpoints(Lists.newArrayList(
//                        // Create a TCP endpoint with no security
//                        new TcpServerEndpoint(
//                                new InetSocketAddress("0.0.0.0", 12686),
//                                Lists.newArrayList(EndpointConfiguration.builder()
//                                        .setTransportProfile(TransportProfile.TCP_UASC_UABINARY)
//                                        .setSecurityPolicy(SecurityPolicy.None)
//                                        .setPath("/my-opcua-server")
//                                        .build())
//                        )
//                ))
//                .build();
//
        // Create the OPC UA server instance
        OpcUaServer server = new OpcUaServer(serverConfig);
//
//        // Add a Username/Password authentication source
//        server.getAuthenticationManager().setIdentityValidator(new UsernameIdentityValidator(
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
        server.startup().get();

        // Run the server
        //server.awaitShutdown();
    }
}
