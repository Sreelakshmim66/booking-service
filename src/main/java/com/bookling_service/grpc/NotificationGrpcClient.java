package com.bookling_service.grpc;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationGrpcClient {

    @GrpcClient("notification-service")
    private NotificationGrpcServiceGrpc.NotificationGrpcServiceBlockingStub notifStub;

    public void sendBookingNotification(String userId, String message, String clientIp) {
        try {
            NotificationRequest request = NotificationRequest.newBuilder()
                    .setUserId(userId)
                    .setMessage(message)
                    .setIp(clientIp != null ? clientIp : "")
                    .build();

            notifStub.sendNotification(request);
            log.info("Notification sent for userId={}", userId);
        } catch (StatusRuntimeException e) {
            // Notifications are non-critical — log the failure but don't break the booking
            log.error("Failed to send notification for userId={}: {}", userId, e.getStatus());
        }
    }
}
