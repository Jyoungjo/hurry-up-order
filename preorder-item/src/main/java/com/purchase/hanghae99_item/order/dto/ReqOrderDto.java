package com.purchase.hanghae99_item.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReqOrderDto {
    List<ReqOrderItemDto> orderItemList;
}
