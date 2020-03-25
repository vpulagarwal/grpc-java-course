package com.github.vipul.grpc.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello from Blog server");

        Server server = ServerBuilder.forPort(50051)
                .addService(new BlogServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("received shut down request ");
            server.shutdown();
            System.out.println("Blog server stopped successfully");
        }));

        server.awaitTermination();
    }
}
