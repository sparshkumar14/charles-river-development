package com.rentalcarcenter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.rentalcarcenter.entities.CarType;
import com.rentalcarcenter.exceptions.NoAvailabilityException;
import com.rentalcarcenter.manager.InventoryManager;
import com.rentalcarcenter.services.CarRentalService;

public class App {
    public static void main(String[] args) {

        Map<CarType, Integer> capacity = new HashMap<>();
        capacity.put(CarType.SEDAN, 3);
        capacity.put(CarType.SUV, 2);
        capacity.put(CarType.VAN, 1);

        InventoryManager inventory = new InventoryManager(capacity);
        CarRentalService rentalService = new CarRentalService(inventory);

        System.out.println("=== CRD Car Rental Simulation Starting ===\n");

        // TEST 1: Standard Success
        try {
            LocalDateTime start = LocalDateTime.now().plusDays(2);
            rentalService.reserveCar(CarType.SUV, start, 5);
            System.out.println("[SUCCESS] SUV reserved for 5 days starting: " + start);
        } catch (Exception e) {
            System.out.println("[FAIL] SUV reservation failed: " + e.getMessage());
        }

        // TEST 2: Scarcity Check (Booking the only Van)
        LocalDateTime vanStart = LocalDateTime.now().plusDays(1);
        try {
            rentalService.reserveCar(CarType.VAN, vanStart, 2);
            System.out.println("[SUCCESS] The single available Van has been reserved.");
        } catch (Exception e) {
            System.out.println("[FAIL] Van reservation failed: " + e.getMessage());
        }

        // TEST 3: Conflict Check (Overlapping the Van booking)
        try {
            System.out.println("[INFO] Attempting to book the same Van for an overlapping date...");
            // Overlaps with the 2-day booking above
            rentalService.reserveCar(CarType.VAN, vanStart.plusDays(1), 1); 
            System.out.println("[FAIL] Error: System allowed a double-booking!");
        } catch (NoAvailabilityException e) {
            System.out.println("[EXPECTED FAILURE] Reservation rejected: " + e.getMessage());
        }

        // TEST 4: Reuse Check (Booking the Van after it is returned)
        try {
            LocalDateTime laterDate = vanStart.plusDays(10);
            rentalService.reserveCar(CarType.VAN, laterDate, 1);
            System.out.println("[SUCCESS] Van reserved for a future non-overlapping date.");
        } catch (Exception e) {
            System.out.println("[FAIL] Van reuse failed: " + e.getMessage());
        }

        System.out.println("\n=== Simulation Complete ===");
    }
}