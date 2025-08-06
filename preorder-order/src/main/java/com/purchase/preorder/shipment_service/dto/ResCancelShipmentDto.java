package com.purchase.preorder.shipment_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResCancelShipmentDto {

    private final List<Long> failedShipmentIds;

    public static ResCancelShipmentDto of(List<Long> failedShipmentIds) {
        return ResCancelShipmentDto.builder()
                .failedShipmentIds(failedShipmentIds)
                .build();
    }
}
