package com.opcuatest.demo.utils;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;

public class KeyLoader {

    public static PrivateKey loadPrivateKeyFromResources(String privateKeyPath) throws IOException {
        ClassPathResource privateKeyResource = new ClassPathResource(privateKeyPath);
        PEMParser privateKeyParser = new PEMParser(new InputStreamReader(privateKeyResource.getInputStream()));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        Object object = privateKeyParser.readObject();
        return converter.getPrivateKey((PrivateKeyInfo) object);
    }
}