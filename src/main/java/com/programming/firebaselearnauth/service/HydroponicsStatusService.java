package com.programming.firebaselearnauth.service;

import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HydroponicsStatusService {

    private static final Logger logger = LoggerFactory.getLogger(HydroponicsStatusService.class);
    private final DatabaseReference statusRef;

    public HydroponicsStatusService() {
        this.statusRef = FirebaseDatabase.getInstance()
                .getReference("test/status");
    }

    public boolean isDeviceAvailable() throws Exception {
        final boolean[] status = {false};

        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean deviceStatus = dataSnapshot.getValue(Boolean.class);
                if (deviceStatus != null) {
                    status[0] = deviceStatus; // true = свободно
                    logger.info("Текущий статус устройства: {}", deviceStatus ? "Свободно" : "Занято");
                } else {
                    logger.warn("Статус устройства отсутствует в базе данных.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                logger.error("Ошибка чтения Firebase: {}", error.getMessage());
                throw new RuntimeException("Ошибка чтения Firebase: " + error.getMessage());
            }
        });

        return status[0];
    }

    public void setDeviceStatus(boolean isAvailable) {
        logger.info("Попытка обновления статуса устройства: {}", isAvailable ? "Свободно" : "Занято");

        statusRef.setValue(isAvailable, (error, ref) -> {
            if (error == null) {
                logger.info("Статус устройства успешно обновлен: {}", isAvailable ? "Свободно" : "Занято");
            } else {
                logger.error("Ошибка обновления статуса устройства: {}", error.getMessage());
            }
        });
    }
}
