package org.example.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.entity.Order;
import org.example.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestParam UUID userId, @RequestParam UUID carId) {
        return orderService.createOrder(userId, carId);
    }
}