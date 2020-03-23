package com.github.vipul.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello from GRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051 )
                .usePlaintext()   // To avoid SSL Error (io.grpc.netty.shaded.io.netty.handler.ssl.NotSslRecordException: not an SSL/TLS record:)
                .build();
        System.out.println("Creating stub");

        // Created greet service client request
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Create a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Vipul")
                .setLastName("Agarwal")
                .build();
        // Do the same for GreetRequest
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        // Call rpc and get GreetResponse
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());

        // Do Something here
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
