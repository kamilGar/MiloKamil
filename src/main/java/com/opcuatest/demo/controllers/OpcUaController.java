package com.opcuatest.demo.controllers;

import com.opcuatest.demo.services.OpcUaDataService;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/opcua")
public class OpcUaController {

    private final OpcUaDataService opcUaDataService;

    @Autowired
    public OpcUaController(OpcUaDataService opcUaDataService) {
        this.opcUaDataService = opcUaDataService;
    }

    @GetMapping("/node/{nodeId}")
    public Object readNode(@PathVariable String nodeId) throws Exception {
        return opcUaDataService.readNode(new NodeId(1, nodeId));
    }
}
