package com.bookling_service.dto;

import com.bookling_service.entity.Booking;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

public class BookingDtos {

    @Data
    public static class CompleteBookingRequest {
        @NotBlank
        private String tripId;

        @NotBlank
        private String userId;

        @NotNull
        private Map<String, Object> userDetails;

        @NotNull
        private Map<String, Object> tripDetails;

        private String clientIp;
    }

    @Data
    public static class BookingResponse {
        private String bookingId;
        private String tripId;
        private String userId;
        private Map<String, Object> userDetails;
        private Map<String, Object> tripDetails;
        private String status;
        private String createdAt;

        public BookingResponse(Booking b, com.fasterxml.jackson.databind.ObjectMapper mapper) {
            this.bookingId = b.getBookingId();
            this.tripId    = b.getTripId();
            this.userId    = b.getUserId();
            this.status    = b.getStatus();
            this.createdAt = b.getCreatedAt() != null ? b.getCreatedAt().toString() : null;
            this.userDetails = parseJson(mapper, b.getUserDetails());
            this.tripDetails = parseJson(mapper, b.getTripDetails());
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> parseJson(com.fasterxml.jackson.databind.ObjectMapper mapper, String json) {
            if (json == null || mapper == null) return null;
            try {
                return mapper.readValue(json, Map.class);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
