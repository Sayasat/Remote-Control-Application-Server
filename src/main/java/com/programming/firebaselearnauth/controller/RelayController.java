package com.programming.firebaselearnauth.controller;

import com.programming.firebaselearnauth.dto.response.RelayStatusResponse;
import com.programming.firebaselearnauth.service.RelayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/relays")
public class RelayController {

    private static final Logger logger = LoggerFactory.getLogger(RelayController.class);
    private final RelayService relayService;

    public RelayController(RelayService relayService) {
        this.relayService = relayService;
    }

    @GetMapping("/{relayId}")
    public ResponseEntity<RelayStatusResponse> getRelayStatus(@PathVariable String relayId) {
        try {
            boolean status = relayService.getRelayStatus(relayId);
            return ResponseEntity.ok(new RelayStatusResponse(status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @PostMapping("/relay-status")
//    public ResponseEntity<RelayStatusResponse> setRelayStatus(@RequestBody RelayStatusRequest request) {
//        try {
//            boolean status = relayService.setRelayStatus(request.getRelayId(), request.isOn());
//            return ResponseEntity.ok(new RelayStatusResponse(status));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }


    @GetMapping("/all")
    public ResponseEntity<Map<String, Boolean>> getAllRelayStatuses() {
        try {
            Map<String, Boolean> statuses = relayService.getAllRelayStatuses();
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyMap());
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<Boolean> updateBatchRelayStatus(@RequestBody Map<String, Boolean> relayStatuses) {
        logger.info("Получен запрос на обновление состояний нескольких реле: {}", relayStatuses);
        boolean result = relayService.setBatchRelayStatus(relayStatuses);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(500).body(false);
        }
    }

}

