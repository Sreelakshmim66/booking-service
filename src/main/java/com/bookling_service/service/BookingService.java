package com.bookling_service.service;


import com.bookling_service.dto.BookingDtos;
import com.bookling_service.entity.Booking;
import com.bookling_service.grpc.NotificationGrpcClient;
import com.bookling_service.grpc.TripGrpcClient;
import com.bookling_service.repository.BookingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripGrpcClient tripGrpcClient;
    private final NotificationGrpcClient notificationGrpcClient;
    private final ObjectMapper objectMapper;

    public BookingDtos.BookingResponse completeBooking(BookingDtos.CompleteBookingRequest req) {

        // 1. Validate tripId + userId via gRPC → trip-service
//        boolean tripValid = tripGrpcClient.validateTrip(req.getTripId(), req.getUserId());
//        if (!tripValid) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    "Trip not found or does not belong to user");
//        }

        Booking booking = new Booking();
        booking.setTripId(req.getTripId());
        booking.setUserId(req.getUserId());

        try {
            booking.setUserDetails(objectMapper.writeValueAsString(req.getUserDetails()));
            booking.setTripDetails(objectMapper.writeValueAsString(req.getTripDetails()));
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userDetails or tripDetails format");
        }

        Booking saved = bookingRepository.save(booking);
        log.info("Booking completed: id={} bookingId={} tripId={}", saved.getId(), saved.getBookingId(), saved.getTripId());

        String message = String.format("Your booking (itinerary %s) has been confirmed for trip %s",
                saved.getBookingId(), saved.getTripId());
        notificationGrpcClient.sendBookingNotification(req.getUserId(), message, req.getClientIp());

        return new BookingDtos.BookingResponse(saved, objectMapper);
    }

    public List<BookingDtos.BookingResponse> getBookingsByTrip(String tripId) {
        return bookingRepository.findByTripId(tripId)
                .stream()
                .map(b -> new BookingDtos.BookingResponse(b, objectMapper))
                .collect(Collectors.toList());
    }

    public BookingDtos.BookingResponse getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return new BookingDtos.BookingResponse(booking, objectMapper);
    }
}
