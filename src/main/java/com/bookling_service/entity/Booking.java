package com.bookling_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
public class Booking {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String bookingId;   // 16-digit numeric itinerary number

    @Column(nullable = false)
    private String tripId;

    @Column(nullable = false)
    private String userId;

    @Column(columnDefinition = "TEXT")
    private String userDetails;

    @Column(columnDefinition = "TEXT")
    private String tripDetails;

    @Column(nullable = false)
    private String status = "CONFIRMED";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.bookingId == null) {
            this.bookingId = String.valueOf(
                ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 10_000_000_000_000_000L)
            );
        }
    }
}

