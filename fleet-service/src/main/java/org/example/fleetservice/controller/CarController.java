package org.example.fleetservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.fleetservice.entity.Car;
import org.example.fleetservice.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Car addCar(@RequestBody Car car) {
        return carService.addCar(car);
    }

    @GetMapping("/{id}")
    public Car getCar(@PathVariable UUID id) {
        return carService.getCarById(id);
    }

    @GetMapping("/available")
    public List<Car> getAvailableCars() {
        return carService.getAvailableCars();
    }

    @PatchMapping("/{id}/availability")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAvailability(@PathVariable UUID id, @RequestParam boolean status) {
        carService.updateAvailability(id, status);
    }
}