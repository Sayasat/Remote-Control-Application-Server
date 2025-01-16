package com.programming.firebaselearnauth.service;

import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class RelayService {

    private static final Logger logger = LoggerFactory.getLogger(RelayService.class);
    private final DatabaseReference relaysRef;

    public RelayService() {
        this.relaysRef = FirebaseDatabase.getInstance().getReference("test/relays");
    }

    public boolean getRelayStatus(String relayId) throws Exception {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        relaysRef.child(relayId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean relayStatus = dataSnapshot.getValue(Boolean.class);
                if (relayStatus != null) {
                    logger.info("Состояние реле {}: {}", relayId, relayStatus ? "Включено" : "Выключено");
                    future.complete(relayStatus);
                } else {
                    logger.warn("Реле {} отсутствует в базе данных.", relayId);
                    future.complete(false); // Значение по умолчанию
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                logger.error("Ошибка чтения Firebase: {}", error.getMessage());
                future.completeExceptionally(new RuntimeException("Ошибка чтения Firebase: " + error.getMessage()));
            }
        });

        return future.get();
    }


//    public boolean setRelayStatus(String relayId, boolean isOn) {
//        CompletableFuture<Boolean> future = new CompletableFuture<>();
//        logger.info("Попытка обновления состояния реле {}: {}", relayId, isOn ? "Включено" : "Выключено");
//
//        relaysRef.child(relayId).setValue(isOn, (error, ref) -> {
//            if (error == null) {
//                logger.info("Состояние реле {} успешно обновлено: {}", relayId, isOn ? "Включено" : "Выключено");
//                future.complete(true);
//            } else {
//                logger.error("Ошибка обновления состояния реле {}: {}", relayId, error.getMessage());
//                future.complete(false);
//            }
//        });
//        try {
//            return future.get();
//        } catch (Exception e) {
//            logger.error("Ошибка при ожидании результата обновления состояния реле {}: {}", relayId, e.getMessage());
//            return false;
//        }
//    }

    public Map<String, Boolean> getAllRelayStatuses() throws Exception {
        CompletableFuture<Map<String, Boolean>> future = new CompletableFuture<>();
        final Map<String, Boolean> relayStatuses = new HashMap<>();

        relaysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot relaySnapshot : dataSnapshot.getChildren()) {
                    String relayId = relaySnapshot.getKey();
                    Boolean status = relaySnapshot.getValue(Boolean.class);
                    relayStatuses.put(relayId, status != null && status);
                }
                logger.info("Состояния всех реле получены: {}", relayStatuses);
                future.complete(relayStatuses);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                logger.error("Ошибка чтения Firebase: {}", error.getMessage());
                future.completeExceptionally(new RuntimeException("Ошибка чтения Firebase: " + error.getMessage()));
            }
        });

        return future.get();
    }

    public boolean setBatchRelayStatus(Map<String, Boolean> relayStatuses) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        logger.info("Попытка обновления состояний реле: {}", relayStatuses);

        Map<String, Object> updateData = new HashMap<>();
        relayStatuses.forEach((relayId, status) -> updateData.put(relayId, status));

        relaysRef.updateChildren(updateData, (error, ref) -> {
            if (error == null) {
                logger.info("Состояния реле успешно обновлены: {}", relayStatuses);
                future.complete(true);
            } else {
                logger.error("Ошибка обновления состояний реле: {}", error.getMessage());
                future.complete(false);
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            logger.error("Ошибка при ожидании результата обновления состояний реле: {}", e.getMessage());
            return false;
        }
    }


}

