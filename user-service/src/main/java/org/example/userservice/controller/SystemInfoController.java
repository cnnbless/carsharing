package org.example.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class SystemInfoController {

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> getServiceVersion() {
        String podName = System.getenv().getOrDefault("HOSTNAME", "local-instance");

        return ResponseEntity.ok(Map.of(
                "service", "user-service",
                "version", "1.1.0",
                "instance", podName,
                "status", "UP"
        ));
    }
}