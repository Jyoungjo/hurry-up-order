package com.purchase.preorder.order_service.shipment;

import com.purchase.preorder.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.purchase.preorder.exception.ExceptionCode.*;
import static com.purchase.preorder.shipment.ShipmentStatus.*;
import static com.purchase.preorder.shipment.ShipmentStatus.ACCEPTANCE;
import static com.purchase.preorder.shipment.ShipmentStatus.READY;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    public Shipment createShipment() {
        return shipmentRepository.save(Shipment.of());
    }

    public void cancelShipment(Shipment shipment) {
        checkBeShipped(shipment);

        shipment.updateStatus(CANCELLED);
        shipmentRepository.save(shipment);
    }

    public void requestReturnShipment(Shipment shipment) {
        checkPossibleReturn(shipment);

        shipment.updateStatus(REQUEST_RETURN);
        shipmentRepository.save(shipment);
    }

    private void checkBeShipped(Shipment shipment) {
        if (!(shipment.getStatus().equals(ACCEPTANCE) || shipment.getStatus().equals(READY))) {
            throw new BusinessException(ALREADY_SHIPPING);
        }
    }

    private void checkPossibleReturn(Shipment shipment) {
        if (!shipment.getStatus().equals(DELIVERED) || shipment.getCreatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
            throw new BusinessException(NO_RETURN);
        }
    }
}
