package com.opcuatest.demo;

import com.opcuatest.demo.utils.MyNamespace;
import org.apache.plc4x.java.opcuaserver.backend.Plc4xNamespace;
import org.apache.plc4x.java.opcuaserver.configuration.Configuration;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static com.opcuatest.demo.utils.KeyLoader.loadPrivateKeyFromResources;

public class MyOpcUaServer {

    public static void main(String[] args) throws InterruptedException, CertificateException, IOException {

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

        MyNamespace myNamespace = new MyNamespace(server, "My Namespace");
        server.getServerNamespace().register(myNamespace);

        String configFile = "src/main/resources/config/server.conf";
        byte[] configFileBytes = Files.readAllBytes(Paths.get(configFile));
        String configFileContent = new String(configFileBytes, StandardCharsets.UTF_8);
        Configuration config = new Configuration();
        config.setConfigFile(configFileContent);

        Plc4xNamespace plc4xNamespace = new Plc4xNamespace(server, config);
        server.getServerNamespace().register(plc4xNamespace);

        // Start the server
        server.startup();
        plc4xNamespace.startup();
        myNamespace.startup();
        System.out.println("OPC UA server is now running.");
        Thread.sleep(1000000);
    }
}
