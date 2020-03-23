package com.github.vipul.grpc.calculator.server;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
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
}
