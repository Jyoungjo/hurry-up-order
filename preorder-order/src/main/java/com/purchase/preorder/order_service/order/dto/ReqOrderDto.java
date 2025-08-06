package com.purchase.preorder.order_service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReqOrderDto {
    private List<ReqOrderItemDto> orderItemList;
}
