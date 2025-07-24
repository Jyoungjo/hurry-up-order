package com.purchase.preorder.shipment_service.shipment.service;

import com.purchase.preorder.shipment_service.dto.ResShipmentCreatedDto;

import java.util.List;

public interface ShipmentService {
    ResShipmentCreatedDto createShipment(Long orderItemId, Long orderId);
    List<ResShipmentCreatedDto> createShipments(List<Long> orderItemIds, Long orderId);
    void cancelShipments(Long orderId, List<Long> orderItemIds);
    void requestReturnShipments(Long orderId, List<Long> orderItemIds);
    void delete(List<Long> orderItemIds);
    void deleteAllByOrderItemId(List<Long> orderItemIds);
    boolean validateCancelable(List<Long> orderItemIds);
    boolean validateReturnable(List<Long> orderItemIds);
}
