package com.github.vipul.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Hello from Calculator client");
        CalculatorClient main = new CalculatorClient();
        main.run();
    }
    private void run(){
        System.out.println("Opening up channel");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051 )
                .usePlaintext()   // To avoid SSL Error (io.grpc.netty.shaded.io.netty.handler.ssl.NotSslRecordException: not an SSL/TLS record:)
                .build();

        //doUnaryCall(channel);
        //doServerStreamingCall(channel);
        doClientStreamingCall(channel);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
    private void doUnaryCall(ManagedChannel channel){
        System.out.println("Creating stub");
        // Create Sum Service client request
        CalculatorServiceGrpc.CalculatorServiceBlockingStub client = CalculatorServiceGrpc.newBlockingStub(channel);

        // UnaryAPI
        // Create a protocol buffer Sum Request
        SumRequest sumRequest = SumRequest.newBuilder()
                .setFirstNumber(32)
                .setSecondNumber(1)
                .build();

        SumResponse sumResponse = client.sumService(sumRequest);
        System.out.println(sumResponse.getSumResult());
    }
    private void doServerStreamingCall(ManagedChannel channel){
        // Stream API
        CalculatorServiceGrpc.CalculatorServiceBlockingStub client = CalculatorServiceGrpc.newBlockingStub(channel);
        PrimeDecompositionRequest primeDecompositionRequest = PrimeDecompositionRequest.newBuilder()
                .setNumber(567890350L)
                .build();
        client.primeDecompositionService(primeDecompositionRequest)
                .forEachRemaining(primeDecompositionResponse -> {
                    System.out.println(primeDecompositionResponse.getPrimeFactor());
                });
    }
    private void doClientStreamingCall(ManagedChannel channel){
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<ComputeAverageRequest> requestObserver= asyncClient.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                // we get a response from the server
                System.out.println("Received a response from the server");
                System.out.println(value.getComputeAverage());
                // onNext will be called only once
            }

            @Override
            public void onError(Throwable t) {
                // we get an error from the server
            }

            @Override
            public void onCompleted() {
                // the server is done sending us data
                // onCompleted will be called right after onNext()
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        // streaming number
        // we send 100 messages to our server (client streaming)
        for (int i = 0; i < 5; i++){
            requestObserver.onNext(ComputeAverageRequest.newBuilder()
                    .setNumber(i+1)
                    .build());
        }

        requestObserver.onCompleted();
        try {
            latch.await(15L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
