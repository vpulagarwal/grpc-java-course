package com.github.vipul.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        //super.greet(request, responseObserver);
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        // Create the response
        String result = "Hello " + firstName;
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        // Send the response
        responseObserver.onNext(response);

        // Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstname= request.getGreeting().getFirstName();
        try {
            for (int i = 0; i < 10; i++) {
                 String result = "Hello " + firstname + ", Response number: "+i;
                 GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                         .setResult(result)
                         .build();
                 responseObserver.onNext(response);

                    Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }

    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        StreamObserver<LongGreetRequest> requestObserver  = new StreamObserver<LongGreetRequest>() {

            String result = "";
            @Override
            public void onNext(LongGreetRequest value) {
                // Client send a message
                result += "Hello "+ value.getGreeting().getFirstName()+ "! ";
            }

            @Override
            public void onError(Throwable t) {
                // Client sends an error
            }

            @Override
            public void onCompleted() {
                // Client is Done
                responseObserver.onNext(
                        LongGreetResponse.newBuilder()
                                .setResult(result)
                                .build()
                );
                responseObserver.onCompleted();
                // This is when we want to return a response (responseObserver)

            }
        };
        return requestObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestObserver = new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello "+ value.getGreeting().getFirstName();
                GreetEveryoneResponse greetEveryoneResponse = GreetEveryoneResponse.newBuilder()
                        .setResult(result)
                        .build();

                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}
