package com.purchase.preorder.item.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReqCreateItemDto {
    @NotBlank(message = "상품명은 필수 입력 대상입니다.")
    @Size(min = 2, max = 20, message = "글자 수는 2 ~ 20자 제한입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9!@#$%^&*(),.?\":{}|<>\\s]*$", message = "한글, 영어, 숫자, 특수문자만 사용 가능합니다.")
    private String name;
    @NotBlank(message = "설명란은 필수 입력 대상입니다.")
    @Size(min = 8, max = 500, message = "글자 수는 8 ~ 500자 제한입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9!@#$%^&*(),.?\":{}|<>\\s]*$", message = "한글, 영어, 숫자, 특수문자만 사용 가능합니다.")
    private String description;
    private Integer quantity;
    private Integer price;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime openTime;
    private Boolean isReserved;
}
