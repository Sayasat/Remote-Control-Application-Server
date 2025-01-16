package com.programming.firebaselearnauth.controller;

import com.programming.firebaselearnauth.dto.request.DeviceStatusRequest;
import com.programming.firebaselearnauth.dto.response.DeviceStatusResponse;
import com.programming.firebaselearnauth.service.HydroponicsStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/device/status")
public class DeviceStatusController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceStatusController.class);
    private final HydroponicsStatusService hydroponicsStatusService;

    public DeviceStatusController(HydroponicsStatusService hydroponicsStatusService) {
        this.hydroponicsStatusService = hydroponicsStatusService;
    }

    @GetMapping
    public ResponseEntity<DeviceStatusResponse> getDeviceStatus() {
        logger.info("Получен запрос на проверку статуса устройства.");
        try {
            DeviceStatusResponse status =
                    new DeviceStatusResponse(hydroponicsStatusService.isDeviceAvailable());
            logger.info("Ответ на запрос: {}", status.isStatus() ? "Свободно" : "Занято");
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Ошибка при проверке статуса устройства: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<DeviceStatusResponse> setDeviceStatus(@RequestBody DeviceStatusRequest deviceStatusRequest) {
        logger.info("Получен запрос на обновление статуса устройства: {}", deviceStatusRequest.isStatus() ? "Свободно" : "Занято");
        hydroponicsStatusService.setDeviceStatus(deviceStatusRequest.isStatus());
        return ResponseEntity.ok(new DeviceStatusResponse(deviceStatusRequest.isStatus()));
    }
}
