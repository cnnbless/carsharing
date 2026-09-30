package org.example.orderservice.service;

import org.example.orderservice.dto.CarDto;
import org.example.orderservice.dto.UserDto;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    private final UUID userId = UUID.randomUUID();
    private final UUID carId = UUID.randomUUID();
    private final String userServiceUrl = "http://localhost:8082/api/users";
    private final String fleetServiceUrl = "http://localhost:8081/api/cars";

    @BeforeEach
    void setUp() {
        // Ініціалізація полів @Value для ізольованого середовища
        ReflectionTestUtils.setField(orderService, "userServiceUrl", userServiceUrl);
        ReflectionTestUtils.setField(orderService, "fleetServiceUrl", fleetServiceUrl);
    }

    @Test
    void createOrder_Success() {
        // Arrange
        UserDto mockUser = new UserDto();
        mockUser.setId(userId);
        mockUser.setIsLicenseValid(true);

        CarDto mockCar = new CarDto();
        mockCar.setId(carId);
        mockCar.setIsAvailable(true);

        Order savedOrder = Order.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .carId(carId)
                .status(OrderStatus.APPROVED)
                .build();

        when(restTemplate.getForObject(userServiceUrl + "/" + userId, UserDto.class)).thenReturn(mockUser);
        when(restTemplate.getForObject(fleetServiceUrl + "/" + carId, CarDto.class)).thenReturn(mockCar);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = orderService.createOrder(userId, carId);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.APPROVED, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(restTemplate, times(1)).patchForObject(
                eq(fleetServiceUrl + "/" + carId + "/availability?status=false"),
                isNull(),
                eq(Void.class)
        );
    }

    @Test
    void createOrder_ThrowsException_WhenUserLicenseInvalid() {
        // Arrange
        UserDto mockUser = new UserDto();
        mockUser.setIsLicenseValid(false);

        when(restTemplate.getForObject(userServiceUrl + "/" + userId, UserDto.class)).thenReturn(mockUser);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.createOrder(userId, carId);
        });

        assertEquals("User license is invalid or user not found", exception.getMessage());
        verify(restTemplate, never()).getForObject(contains("cars"), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ThrowsException_WhenCarNotAvailable() {
        // Arrange
        UserDto mockUser = new UserDto();
        mockUser.setIsLicenseValid(true);

        CarDto mockCar = new CarDto();
        mockCar.setIsAvailable(false);

        when(restTemplate.getForObject(userServiceUrl + "/" + userId, UserDto.class)).thenReturn(mockUser);
        when(restTemplate.getForObject(fleetServiceUrl + "/" + carId, CarDto.class)).thenReturn(mockCar);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.createOrder(userId, carId);
        });

        assertEquals("Car is not available", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }
}