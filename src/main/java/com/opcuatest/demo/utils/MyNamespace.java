package com.opcuatest.demo.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.eclipse.milo.examples.server.ExampleNamespace.NAMESPACE_URI;

public class MyNamespace extends ManagedNamespaceWithLifecycle {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public MyNamespace(OpcUaServer server, String namespaceUri) {
        super(server, namespaceUri);
    }

    private static ScheduledExecutorService getExecutorService() {
        return Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("namespace-%d").build());
    }

    @Override
    public void onDataItemsCreated(List<DataItem> list) {

    }

    @Override
    public void onDataItemsModified(List<DataItem> list) {

    }

    @Override
    public void onDataItemsDeleted(List<DataItem> list) {

    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> list) {

    }
}