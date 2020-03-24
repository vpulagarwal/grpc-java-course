package com.github.vipul.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sumService(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        int firstNumber = request.getFirstNumber();
        int secondNumber = request.getSecondNumber();

        // Create the response
        int result = firstNumber + secondNumber;
        SumResponse response = SumResponse.newBuilder()
                .setSumResult(result)
                .build();
        // Send the response
        responseObserver.onNext(response);
        // Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void primeDecompositionService(PrimeDecompositionRequest request, StreamObserver<PrimeDecompositionResponse> responseObserver) {
        int k = 2;
        Long number = request.getNumber();

        while (number>1){
            if (number % k == 0){
                number = number/ k;
                PrimeDecompositionResponse response = PrimeDecompositionResponse.newBuilder()
                        .setPrimeFactor(k)
                        .build();
                responseObserver.onNext(response);
            }else{
                k = k+1;
                //System.out.println(k);
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        StreamObserver<ComputeAverageRequest> requestObserver = new StreamObserver<ComputeAverageRequest>() {
            Long sum =0L;
            int count=0;
            @Override
            public void onNext(ComputeAverageRequest value) {
                // Client sends number in streams and server calculate the sum of number
                System.out.println("Received number from client");
                sum += value.getNumber();
                count+=1;
                //System.out.println("Received number= "+value.getNumber()+ ", Sum after number "+count+" = "+sum);
            }

            @Override
            public void onError(Throwable t) {
                // handle errors
            }

            @Override
            public void onCompleted() {
                // Client is done sending numbers
                double average = (double) (sum/count);
                responseObserver.onNext(ComputeAverageResponse.newBuilder()
                        .setComputeAverage(average)
                        .build());

                responseObserver.onCompleted();

            }
        };

        return requestObserver;
    }

    @Override
    public StreamObserver<FindMaximumRequest> findMaximum(StreamObserver<FindMaximumResponse> responseObserver) {
        StreamObserver<FindMaximumRequest> requestObserver = new StreamObserver<FindMaximumRequest>() {
            int currentMaximum = 0;
            @Override
            public void onNext(FindMaximumRequest value) {
                int currentNumber = value.getNumber();
                System.out.println("Current Number "+ currentNumber);
                if(currentNumber>currentMaximum){
                    currentMaximum = currentNumber;
                    responseObserver.onNext(FindMaximumResponse.newBuilder()
                            .setMaximum(currentMaximum)
                            .build()
                    );
                }
                else{
                    // Do nothing
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                // send the current last maximum
                responseObserver.onNext(
                        FindMaximumResponse.newBuilder()
                                .setMaximum(currentMaximum)
                                .build());
                // the server is done sending data
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}
