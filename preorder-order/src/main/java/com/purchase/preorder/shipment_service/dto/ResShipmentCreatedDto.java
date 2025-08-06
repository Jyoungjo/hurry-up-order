package com.purchase.preorder.shipment_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResShipmentCreatedDto {
    private final Long shipmentId;
    private final Long orderItemId;

    public static ResShipmentCreatedDto of(Long shipmentId, Long orderItemId) {
        return ResShipmentCreatedDto.builder()
                .shipmentId(shipmentId)
                .orderItemId(orderItemId)
                .build();
    }
}
