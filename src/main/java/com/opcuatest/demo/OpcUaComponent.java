package com.opcuatest.demo;

import com.opcuatest.demo.config.OpcUaConfiguration;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpcUaComponent {

    private final OpcUaClient opcUaClient;

    @Autowired
    public OpcUaComponent(OpcUaConfiguration opcUaConfiguration) throws Exception {
        this.opcUaClient = opcUaConfiguration.opcUaClient();
    }

    public OpcUaClient getOpcUaClient() {
        return opcUaClient;
    }
}
