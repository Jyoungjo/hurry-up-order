package com.purchase.hanghae99_user.shipment;

import com.purchase.hanghae99_core.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.purchase.hanghae99_core.exception.ExceptionCode.ALREADY_SHIPPING;
import static com.purchase.hanghae99_core.exception.ExceptionCode.NO_RETURN;
import static com.purchase.hanghae99_user.shipment.ShipmentStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {
    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    private Shipment shipment;

    @BeforeEach
    void init() {
        shipment = Shipment.builder()
                .id(1L)
                .status(ACCEPTANCE)
                .createdAt(LocalDateTime.of(2024, 6, 29, 16, 4))
                .build();
    }

    // CREATE
    @DisplayName("배송 정보 생성 기능 성공")
    @Test
    void 배송_정보_생성_기능_성공() {
        // given
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // when
        Shipment res = shipmentService.createShipment();

        // then
        assertThat(res.getId()).isEqualTo(shipment.getId());
    }

    // UPDATE
    @DisplayName("배송 상태 변경 기능 성공 - 주문 취소")
    @Test
    void 주문_취소_변경_성공() {
        // given
        ShipmentStatus status = shipment.getStatus();
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // when
        shipmentService.cancelShipment(shipment);

        // then
        assertThat(shipment.getStatus()).isNotEqualTo(status);
        assertThat(shipment.getStatus()).isEqualTo(CANCELLED);
    }

    // UPDATE
    @DisplayName("배송 상태 변경 기능 실패 - 주문 취소(이미 배송중인 경우)")
    @Test
    void 주문_취소_변경_실패_이미_배송중인_경우() {
        // given
        shipment.updateStatus(SHIPPING);

        // when

        // then
        assertThatThrownBy(() -> shipmentService.cancelShipment(shipment))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ALREADY_SHIPPING.getMessage());
    }

    // UPDATE
    @DisplayName("배송 상태 변경 기능 성공 - 반품 신청")
    @Test
    void 반품_신청_변경_성공() {
        // given
        shipment.updateStatus(DELIVERED);

        ShipmentStatus status = shipment.getStatus();
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // when
        shipmentService.requestReturnShipment(shipment);

        // then
        assertThat(shipment.getStatus()).isNotEqualTo(status);
        assertThat(shipment.getStatus()).isEqualTo(REQUEST_RETURN);
    }

    // UPDATE
    @DisplayName("배송 상태 변경 기능 실패 - 반품 신청(배송 완료가 아닌 경우)")
    @Test
    void 반품_신청_변경_실패_배송_완료가_아닌_경우() {
        // given

        // when

        // then
        assertThatThrownBy(() -> shipmentService.requestReturnShipment(shipment))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NO_RETURN.getMessage());
    }

    // UPDATE
    @DisplayName("배송 상태 변경 기능 실패 - 반품 신청(반품 날짜가 지난 경우)")
    @Test
    void 반품_신청_변경_실패_날짜가_지난_경우() {
        // given
        Shipment newShipment = Shipment.builder()
                .id(1L)
                .status(DELIVERED)
                .createdAt(LocalDateTime.of(2024, 6, 27, 16, 4))
                .build();

        // when

        // then
        assertThatThrownBy(() -> shipmentService.requestReturnShipment(newShipment))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NO_RETURN.getMessage());
    }
}
