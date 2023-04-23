package com.opcuatest.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.eclipse.milo.opcua.stack.client.transport.UaTransport;
import org.eclipse.milo.opcua.stack.core.serialization.UaRequestMessage;
import org.eclipse.milo.opcua.stack.core.serialization.UaResponseMessage;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

public class MyTransport implements UaTransport {

    @Value("opcua.server.bind-port")
    private String serverPort;

    @Value("opcua.server.bind-addresses")
    private String serverHost;

    public void setEndpoint(EndpointDescription endpoint) {

    }

    @Override
    public CompletableFuture<UaTransport> connect() {
        CompletableFuture<UaTransport> future = new CompletableFuture<>();
        // TODO: implement the connect logic
        future.complete(this);
        return future;
    }

    @Override
    public CompletableFuture<UaTransport> disconnect() {
        return null;
    }

    @Override
    public CompletableFuture<UaResponseMessage> sendRequest(UaRequestMessage uaRequestMessage) {
        byte[] bytes = new byte[3];
        CompletableFuture<byte[]> future = sendRequest(bytes);
        return new CompletableFuture<>();
        //TODO:  implement send request logic
    }

    public CompletableFuture<byte[]> sendRequest(byte[] request) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        try {
            // create a new socket and connect to the server
            Socket socket = new Socket(serverHost, Integer.parseInt(serverPort));

            // write the request to the socket
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(request);

            // read the response from the socket
            InputStream inputStream = socket.getInputStream();
            byte[] response = inputStream.readAllBytes();

            // close the socket
            socket.close();

            // complete the future with the response
            future.complete(response);
        } catch (IOException e) {
            // if there was an error, complete the future exceptionally
            future.complete(new byte[0]);
        }
        return future;
    }

    public CompletableFuture<Void> sendRequestAsync(byte[] request) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        // TODO: implement the sendRequestAsync logic
        future.complete(null);
        return future;
    }

    public URI getUri() {
        return null;
    }

    public boolean isConnected() {
        return true;
    }

    public boolean isSecureChannelOpen() {
        return true;
    }
}
