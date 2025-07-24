package com.purchase.preorder.shipment_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResReturnRequestShipmentDto {

    private final List<Long> failedShipmentIds;

    public static ResReturnRequestShipmentDto of(List<Long> failedShipmentIds) {
        return ResReturnRequestShipmentDto.builder()
                .failedShipmentIds(failedShipmentIds)
                .build();
    }
}
