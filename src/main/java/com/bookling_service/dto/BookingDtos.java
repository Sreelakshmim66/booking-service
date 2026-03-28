package com.bookling_service.dto;

import com.bookling_service.entity.Booking;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class BookingDtos {

    @Data
    public static class CreateBookingRequest {
        @NotBlank
        private String tripId;

        @NotBlank
        private String userId;

        @NotNull
        private Booking.BookingType type;   // HOTEL | FLIGHT | ACTIVITY

        private String details;

        private String clientIp;            // passed by orchestrator from the HTTP request
    }

    @Data
    public static class BookingResponse {
        private String id;
        private String tripId;
        private String userId;
        private String type;
        private String details;
        private String status;
        private String createdAt;

        public BookingResponse(Booking b) {
            this.id        = b.getId();
            this.tripId    = b.getTripId();
            this.userId    = b.getUserId();
            this.type      = b.getType().name();
            this.details   = b.getDetails();
            this.status    = b.getStatus();
            this.createdAt = b.getCreatedAt() != null ? b.getCreatedAt().toString() : null;
        }
    }
}
