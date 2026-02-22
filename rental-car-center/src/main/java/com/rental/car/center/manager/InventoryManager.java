package com.rental.car.center.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rental.car.center.entities.CarType;
import com.rental.car.center.entities.Reservation;

public class InventoryManager {
    // Total cars owned by the company
    private final Map<CarType, Integer> totalCapacity;
    
    // Record of all confirmed bookings
    private final List<Reservation> confirmedReservations = new ArrayList<>();

    public InventoryManager(Map<CarType, Integer> capacity) {
        this.totalCapacity = capacity;
    }

    // Since multiple users might try to book the last van at the same time,
    // both the availability check and the reservation addition must be synchronized aka atomic
    public synchronized boolean isAvailable(CarType type, LocalDateTime start, LocalDateTime end) {
        int capacity = totalCapacity.getOrDefault(type, 0);
        
        // Count how many cars of this type are already booked during THIS specific window
        long takenCount = confirmedReservations.stream()
            .filter(res -> res.getCarType() == type)
            .filter(res -> overlaps(res.getStartTime(), res.getEndTime(), start, end))
            .count();

        return takenCount < capacity;
    }

    private boolean overlaps(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        // Standard interval overlap formula: (StartA < EndB) AND (StartB < EndA)
        return s1.isBefore(e2) && s2.isBefore(e1);
    }

    // prevent race conditions
    public synchronized void addReservation(Reservation res) {
        confirmedReservations.add(res);
    }
}