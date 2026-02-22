package com.rental.car.center.entities;

import java.time.LocalDateTime;

public class Reservation {

	private CarType carType;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	public Reservation(CarType carType, LocalDateTime startTime, LocalDateTime endTime) {
		this.carType = carType;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public CarType getCarType() {
		return carType;
	}

	public void setCarType(CarType carType) {
		this.carType = carType;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
}
