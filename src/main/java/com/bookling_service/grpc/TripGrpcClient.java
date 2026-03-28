package com.bookling_service.grpc;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TripGrpcClient {

    @GrpcClient("trip-service")
    private TripGrpcServiceGrpc.TripGrpcServiceBlockingStub tripStub;

    public boolean validateTrip(String tripId, String userId) {
        try {
            ValidateTripRequest request = ValidateTripRequest.newBuilder()
                    .setTripId(tripId)
                    .setUserId(userId)
                    .build();

            ValidateTripResponse response = tripStub.validateTrip(request);
            return response.getValid();
        } catch (StatusRuntimeException e) {
            log.error("gRPC call to trip-service failed for tripId={}: {}", tripId, e.getStatus());
            return false;
        }
    }
}
