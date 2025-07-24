package com.purchase.preorder.order_service.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCancelOrderDto {

    private List<Long> orderItemIds;

    @NotBlank
    @Size(min = 1, max = 200)
    private String cancelReason;
}
