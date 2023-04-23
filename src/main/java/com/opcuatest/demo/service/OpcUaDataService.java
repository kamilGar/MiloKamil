package com.opcuatest.demo.service;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpcUaDataService {

    private final OpcUaClient opcUaClient;

    @Autowired
    public OpcUaDataService(OpcUaClient opcUaClient) {
        this.opcUaClient = opcUaClient;
    }

    public void getData() throws Exception {
        // kod pobierający dane z serwera OPC UA
    }
    public Object readNode(NodeId nodeId) throws Exception {
        // Odczytanie wartości węzła z serwera OPC UA
        DataValue dataValue = opcUaClient.readValue(0, TimestampsToReturn.Both, nodeId).get();

        // Zwrócenie wartości węzła jako obiektu
        return dataValue.getValue().getValue();
    }

}
