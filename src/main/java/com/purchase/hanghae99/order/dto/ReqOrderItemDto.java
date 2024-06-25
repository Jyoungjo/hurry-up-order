package com.purchase.hanghae99.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReqOrderItemDto {
    @NotBlank
    private Long itemId;
    private Integer itemCount;
}
