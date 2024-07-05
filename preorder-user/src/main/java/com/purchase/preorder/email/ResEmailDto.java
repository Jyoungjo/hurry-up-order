package com.purchase.preorder.email;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResEmailDto {
    private Boolean status;
    private String message;
}
