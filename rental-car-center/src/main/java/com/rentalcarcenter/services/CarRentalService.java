package com.rentalcarcenter.services;

import java.time.LocalDateTime;

import com.rentalcarcenter.entities.CarType;
import com.rentalcarcenter.entities.Reservation;
import com.rentalcarcenter.exceptions.NoAvailabilityException;
import com.rentalcarcenter.manager.InventoryManager;

public class CarRentalService {
	private final InventoryManager inventoryManager;

	// Dependency Injection
	public CarRentalService(InventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public Reservation reserveCar(CarType type, LocalDateTime start, int durationInDays) throws NoAvailabilityException {
		// 1. Validation Layer
		validateRequest(start, durationInDays);

		LocalDateTime end = start.plusDays(durationInDays);

		// 2. Coordination with Inventory Manager
		// Because InventoryManager methods are synchronized, this is thread-safe
		synchronized (inventoryManager) {
			if (inventoryManager.isAvailable(type, start, end)) {
				Reservation reservation = new Reservation(type, start, end);
				inventoryManager.addReservation(reservation);
				return reservation;
			} else {
				throw new NoAvailabilityException("No " + type + " available for the selected dates.");
			}
		}
	}

	// TODO: Add a cancellation method that removes a reservation and updates availability accordingly

	private void validateRequest(LocalDateTime start, int duration) {
		if (start.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Reservation start date cannot be in the past.");
		}
		if (duration <= 0) {
			throw new IllegalArgumentException("Duration must be at least 1 day.");
		}
	}
}
