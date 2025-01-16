package com.programming.firebaselearnauth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelayStatusRequest {
    private String relayId;
    private boolean isOn;

}
