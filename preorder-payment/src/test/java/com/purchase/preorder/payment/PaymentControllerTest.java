package com.purchase.preorder.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.preorder.dto.ReqPaymentDto;
import com.purchase.preorder.dto.ResPaymentDto;
import com.purchase.preorder.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.purchase.preorder.exception.ExceptionCode.CANCEL_PAYMENT;
import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_PAYMENT;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureRestDocs
@ActiveProfiles("test")
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    // CREATE
    @DisplayName("결제 시도 성공")
    @Test
    void 결제_시도_성공() throws Exception {
        // given
        ReqPaymentDto req = new ReqPaymentDto(1L, 10000);

        // when
        ResPaymentDto res = new ResPaymentDto(1L, true);
        when(paymentService.initiatePayment(any(ReqPaymentDto.class))).thenReturn(res);

        // then
        mockMvc.perform(post("/payment-service/api/v1/payments")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("payment/결제_시도/성공"));
    }

    // CREATE
    @DisplayName("결제 시도 실패 - 사용자 변심")
    @Test
    void 결제_시도_실패_사용자_변심() throws Exception {
        // given
        ReqPaymentDto req = new ReqPaymentDto(1L, 10000);

        // when
        when(paymentService.initiatePayment(any(ReqPaymentDto.class)))
                .thenThrow(new BusinessException(CANCEL_PAYMENT));

        // then
        mockMvc.perform(post("/payment-service/api/v1/payments")
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CANCEL_PAYMENT.getMessage()))
                .andDo(print())
                .andDo(document("payment/결제_시도/실패/사용자_변심"));
    }

    // UPDATE
    @DisplayName("결제 완료 성공")
    @Test
    void 결제_완료_성공() throws Exception {
        // given
        Long paymentId = 1L;

        // when
        ResPaymentDto res = new ResPaymentDto(1L, true);
        when(paymentService.completePayment(anyLong())).thenReturn(res);

        // then
        mockMvc.perform(put("/payment-service/api/v1/payments/{paymentId}", paymentId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("payment/결제_완료/성공",
                        pathParameters(parameterWithName("paymentId").description("결제 id"))
                ));
    }

    // UPDATE
    @DisplayName("결제 완료 실패 - 사용자 변심")
    @Test
    void 결제_완료_실패_사용자_변심() throws Exception {
        // given
        Long paymentId = 1L;

        // when
        when(paymentService.completePayment(anyLong()))
                .thenThrow(new BusinessException(CANCEL_PAYMENT));

        // then
        mockMvc.perform(put("/payment-service/api/v1/payments/{paymentId}", paymentId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CANCEL_PAYMENT.getMessage()))
                .andDo(print())
                .andDo(document("payment/결제_완료/실패/사용자_변심",
                        pathParameters(parameterWithName("paymentId").description("결제 id"))
                ));
    }

    // UPDATE
    @DisplayName("결제 완료 실패 - 존재하지 않는 결제 정보")
    @Test
    void 결제_완료_실패_존재하지_않는_결제_정보() throws Exception {
        // given
        Long paymentId = 1L;

        // when
        when(paymentService.completePayment(anyLong()))
                .thenThrow(new BusinessException(NOT_FOUND_PAYMENT));

        // then
        mockMvc.perform(put("/payment-service/api/v1/payments/{paymentId}", paymentId)
                        .header("Cookie", "accessToken={access_token};refreshToken={refresh_token};")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_PAYMENT.getMessage()))
                .andDo(print())
                .andDo(document("payment/결제_완료/실패/존재하지_않는_결제_정보",
                        pathParameters(parameterWithName("paymentId").description("결제 id"))
                ));
    }
}
