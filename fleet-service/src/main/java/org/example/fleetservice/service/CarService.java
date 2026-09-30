package org.example.fleetservice.service;

import lombok.RequiredArgsConstructor;
import org.example.fleetservice.entity.Car;
import org.example.fleetservice.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    public Car getCarById(UUID id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
    }

    public List<Car> getAvailableCars() {
        return carRepository.findByIsAvailableTrue();
    }

    @Transactional
    public void updateAvailability(UUID id, boolean status) {
        Car car = getCarById(id);
        car.setIsAvailable(status);
        carRepository.save(car);
    }
}