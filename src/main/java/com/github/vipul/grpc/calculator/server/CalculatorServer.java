package com.github.vipul.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello from Calculator server");

        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("received shut down request ");
            server.shutdown();
            System.out.println("Server stopped successfully");
        }));

        server.awaitTermination();
    }
}
