package com.bookling_service.controller;


import com.bookling_service.dto.BookingDtos;
import com.bookling_service.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // POST /api/bookings/completeBooking
    @PostMapping("/completeBooking")
    public ResponseEntity<BookingDtos.BookingResponse> completeBooking(
            @Valid @RequestBody BookingDtos.CompleteBookingRequest req,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        req.setClientIp(clientIp);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.completeBooking(req));
    }

    // GET /api/bookings/trip/{tripId}
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<BookingDtos.BookingResponse>> getBookingsByTrip(@PathVariable String tripId) {
        return ResponseEntity.ok(bookingService.getBookingsByTrip(tripId));
    }

    // GET /api/bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtos.BookingResponse> getBookingById(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
