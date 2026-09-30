package org.example.orderservice.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private Boolean isLicenseValid;
}