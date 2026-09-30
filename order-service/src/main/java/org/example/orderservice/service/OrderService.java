package org.example.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.CarDto;
import org.example.orderservice.dto.UserDto;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${services.user.url}")
    private String userServiceUrl;

    @Value("${services.fleet.url}")
    private String fleetServiceUrl;

    @Transactional
    public Order createOrder(UUID userId, UUID carId) {
        // 1. Валідація користувача
        UserDto user = restTemplate.getForObject(userServiceUrl + "/" + userId, UserDto.class);
        if (user == null || !Boolean.TRUE.equals(user.getIsLicenseValid())) {
            throw new IllegalStateException("User license is invalid or user not found");
        }

        // 2. Валідація автомобіля
        CarDto car = restTemplate.getForObject(fleetServiceUrl + "/" + carId, CarDto.class);
        if (car == null || !Boolean.TRUE.equals(car.getIsAvailable())) {
            throw new IllegalStateException("Car is not available");
        }

        // 3. Створення ордера
        Order order = Order.builder()
                .userId(userId)
                .carId(carId)
                .status(OrderStatus.APPROVED)
                .build();
        order = orderRepository.save(order);

        // 4. Оновлення статусу автомобіля
        restTemplate.patchForObject(fleetServiceUrl + "/" + carId + "/availability?status=false", null, Void.class);

        return order;
    }
}