package com.opcuatest.demo.config;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;

import com.opcuatest.demo.MyTransport;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.X509IdentityProvider;
import org.eclipse.milo.opcua.stack.client.UaStackClient;
import org.eclipse.milo.opcua.stack.client.UaStackClientConfig;
import org.eclipse.milo.opcua.stack.client.transport.UaTransport;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ApplicationType;
import org.eclipse.milo.opcua.stack.core.types.structured.ApplicationDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.function.Function;
import java.io.FileInputStream;

import static com.opcuatest.demo.KeyLoader.loadPrivateKeyFromResources;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

@Configuration
public class OpcUaConfiguration {

    @Value("${opcua.server.endpointUrl}")
    private String endpointUrl;

    @Value("opcua.server.application-uri")
    private String appUri;

    @Value("opcua.server.application-name")
    private String appName;

    @Value("${opcua.server.username}")
    private String username;

    @Value("${opcua.server.password}")
    private String password;


    @Bean
    public OpcUaClient opcUaClient() throws Exception {

        // wczytywanie certyfikatu z pliku server_cert.pem
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream certFile = new FileInputStream("src/main/resources/security/server_cert.pem");
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(certFile);

        // utworzenie łańcucha certyfikatów
        X509Certificate[] certChain = new X509Certificate[1];
        certChain[0] = certificate;

        // wczytywanie klucza prywatnego z pliku server_key.pem
        PrivateKey privateKey = loadPrivateKeyFromResources("security/server_key.pem");
        KeyPair keyPair = new KeyPair(certificate.getPublicKey(), privateKey);

        String productUri = "urn:my-product";
        ApplicationDescription appDescription = new ApplicationDescription(
                appUri,
                productUri,
                LocalizedText.english(appName),
                ApplicationType.Client,
                null, null, null
        );

        EndpointDescription endpoint = EndpointDescription
                .builder()
                .endpointUrl(endpointUrl)
                .server(appDescription)
                .build();

        UaTransport transport = new MyTransport();

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setRequestTimeout(uint(5000))
                .setEndpoint(endpoint)
                .setApplicationUri(appUri)
                .setApplicationName(new LocalizedText(appName))
                .setCertificate(certificate)
                .setKeyPair(keyPair)
                .setCertificateChain(certChain)
                .setIdentityProvider(new X509IdentityProvider(certificate, privateKey))
                .build();

        UaStackClientConfig uaConfig = UaStackClientConfig.builder()
                .setEndpoint(endpoint)
                .setRequestTimeout(uint(5000))
                .build();

        Function<UaStackClient, UaTransport> transportFactory = uaStackClient -> transport;
        UaStackClient stackClient = new UaStackClient(uaConfig, transportFactory);

        OpcUaClient opcUaClient = new OpcUaClient(config, stackClient);
        System.out.println(opcUaClient.getSession());
        //opcUaClient.connect().get();

        return opcUaClient;
    }

}
