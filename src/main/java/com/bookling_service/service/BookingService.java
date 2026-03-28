package com.bookling_service.service;


import com.bookling_service.dto.BookingDtos;
import com.bookling_service.entity.Booking;
import com.bookling_service.grpc.NotificationGrpcClient;
import com.bookling_service.grpc.TripGrpcClient;
import com.bookling_service.repository.BookingRepository;
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

    public BookingDtos.BookingResponse createBooking(BookingDtos.CreateBookingRequest req) {
        // 1. Validate tripId + userId via gRPC → trip-service
        boolean tripValid = tripGrpcClient.validateTrip(req.getTripId(), req.getUserId());
        if (!tripValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Trip not found or does not belong to user");
        }

        // 2. Persist the booking
        Booking booking = new Booking();
        booking.setTripId(req.getTripId());
        booking.setUserId(req.getUserId());
        booking.setType(req.getType());
        booking.setDetails(req.getDetails());

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created: id={} type={} tripId={}", saved.getId(), saved.getType(), saved.getTripId());

        // 3. Fire-and-forget notification via gRPC → notification-service
        String message = String.format("Your %s booking has been confirmed for trip %s",
                saved.getType().name().toLowerCase(), saved.getTripId());
        notificationGrpcClient.sendBookingNotification(req.getUserId(), message, req.getClientIp());

        return new BookingDtos.BookingResponse(saved);
    }

    public List<BookingDtos.BookingResponse> getBookingsByTrip(String tripId) {
        return bookingRepository.findByTripId(tripId)
                .stream()
                .map(BookingDtos.BookingResponse::new)
                .collect(Collectors.toList());
    }

    public BookingDtos.BookingResponse getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return new BookingDtos.BookingResponse(booking);
    }
}
