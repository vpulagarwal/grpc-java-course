package com.github.vipul.grpc.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Hello from Calculator client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("Creating stub");

        // Create Sum Service client request
        CalculatorServiceGrpc.CalculatorServiceBlockingStub client = CalculatorServiceGrpc.newBlockingStub(channel);

        // Create a protocol buffer Sum Request
        SumRequest sumRequest = SumRequest.newBuilder()
                .setFirstNumber(32)
                .setSecondNumber(1)
                .build();

        SumResponse sumResponse = client.sumService(sumRequest);
        System.out.println(sumResponse.getSumResult());

        System.out.println("Shutting down channel");
        channel.shutdown();

    }
}
