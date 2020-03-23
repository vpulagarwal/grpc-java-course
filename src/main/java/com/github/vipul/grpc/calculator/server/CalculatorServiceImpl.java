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
}
