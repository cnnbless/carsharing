package org.example.orderservice.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class CarDto {
    private UUID id;
    private Boolean isAvailable;
}