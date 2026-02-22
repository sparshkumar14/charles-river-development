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
    // ...existing code...
}
