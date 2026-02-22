package com.rentalcarcenter.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rentalcarcenter.entities.CarType;
import com.rentalcarcenter.entities.Reservation;
import com.rentalcarcenter.exceptions.NoAvailabilityException;
import com.rentalcarcenter.manager.InventoryManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CarRentalServiceTest {

    private CarRentalService rentalService;
    private InventoryManager inventoryManager;

    @Before
    public void setUp() {
        // Initialize with the limits requested in the assessment
        Map<CarType, Integer> capacity = new HashMap<>();
        capacity.put(CarType.SEDAN, 3);
        capacity.put(CarType.SUV, 2);
        capacity.put(CarType.VAN, 1);

        inventoryManager = new InventoryManager(capacity);
        rentalService = new CarRentalService(inventoryManager);
    }

    @Test
    public void testSuccessfulBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        Reservation res = rentalService.reserveCar(CarType.SUV, start, 3);

        Assert.assertNotNull("Reservation should not be null", res);
        Assert.assertEquals(CarType.SUV, res.getCarType());
    }

    @Test(expected = NoAvailabilityException.class)
    public void testInventoryLimitFails() throws NoAvailabilityException {
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        // We only have 1 Van. First one succeeds.
        rentalService.reserveCar(CarType.VAN, start, 1);

        // Second one should throw NoAvailabilityException
        rentalService.reserveCar(CarType.VAN, start, 1);
    }

    @Test
    public void testNonOverlappingBookingsSucceed() throws Exception {
        LocalDateTime monday = LocalDateTime.now().plusDays(1);
        rentalService.reserveCar(CarType.VAN, monday, 1);

        // Friday is far enough away that it should not overlap
        LocalDateTime friday = LocalDateTime.now().plusDays(5);
        Reservation res = rentalService.reserveCar(CarType.VAN, friday, 1);
        
        Assert.assertNotNull(res);
    }

    @Test(expected = NoAvailabilityException.class)
    public void testOverlappingBookingFails() throws NoAvailabilityException {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        // Reservation for 3 days (Day 2, 3, 4)
        rentalService.reserveCar(CarType.VAN, start, 3);

        // Attempting to book Day 3 (overlaps)
        LocalDateTime overlap = start.plusDays(1);
        rentalService.reserveCar(CarType.VAN, overlap, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPastDateFails() throws Exception {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        rentalService.reserveCar(CarType.SEDAN, past, 2);
    }

    @Test
    public void testConcurrencyStress() throws InterruptedException {
        final int numberOfThreads = 10;
        // We only have 1 VAN in the setup
        final CarType type = CarType.VAN; 
        final LocalDateTime start = LocalDateTime.now().plusDays(10);
        final int duration = 2;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        // This latch ensures all threads wait and start at the EXACT same moment
        final CountDownLatch latch = new CountDownLatch(1); 

        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(() -> {
                try {
                    latch.await(); // All threads pause here until latch hits 0
                    rentalService.reserveCar(type, start, duration);
                    successCount.incrementAndGet();
                } catch (NoAvailabilityException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    // Should not happen with our current logic
                }
            });
        }

        latch.countDown(); 

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Assert.assertEquals("Only 1 reservation should succeed for a single Van", 1, successCount.get());
        Assert.assertEquals("9 reservations should have failed due to unavailability", 9, failureCount.get());
    }

}
