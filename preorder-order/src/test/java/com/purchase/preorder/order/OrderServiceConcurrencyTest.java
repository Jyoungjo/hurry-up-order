//package com.purchase.preorder.order;
//
//import com.purchase.preorder.order.dto.ReqLimitedOrderDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockCookie;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(properties = {
//        "spring.cloud.gateway.enabled=false"
//})
//@ActiveProfiles("test")
//@AutoConfigureWireMock(port = 0)
//public class OrderServiceConcurrencyTest {
//    @Autowired
//    private OrderService orderService;
//
//    private static final int NUM_THREADS = 1000;
//    private static final int STOCK_QUANTITY = 10;
//
//    @Test
//    @DisplayName("동시성 테스트")
//    public void testConcurrentOrderCreationWithLimitedStock() throws Exception {
//        CountDownLatch doneSignal = new CountDownLatch(NUM_THREADS);
//        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
//
//        AtomicInteger successCount = new AtomicInteger();
//        AtomicInteger failCount = new AtomicInteger();
//
//        stubFor(get(urlPathEqualTo("/item-service/api/v1/stocks/items/1"))
//                .willReturn(aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("{\"quantity\": 10}")));
//
//        stubFor(post(urlPathEqualTo("/item-service/api/v1/internal/stocks"))
//                .willReturn(aResponse()
//                        .withStatus(HttpStatus.NO_CONTENT.value())
//                        .withTransformerParameter("itemId", 1L)
//                        .withTransformerParameter("quantity", 1)));
//
//        stubFor(put(urlPathEqualTo("/item-service/api/v1/internal/stocks"))
//                .willReturn(aResponse()
//                        .withStatus(HttpStatus.NO_CONTENT.value())
//                        .withTransformerParameter("itemId", 1L)
//                        .withTransformerParameter("quantity", 1)));
//
//        stubFor(get(urlPathEqualTo("/user-service/api/v1/internal/users"))
//                .willReturn(aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("{\"id\": 1, \"name\": \"이름\", \"email\": \"test@email.com\", " +
//                                "\"emailVerifiedAt\": \"2024-07-12T09:00:00Z\", \"password\": \"1234\", " +
//                                "\"address\": \"주소\", \"phoneNumber\": \"010-1234-1234\", " +
//                                "\"role\": \"CERTIFIED\", \"deletedAt\": null}")));
//
//        stubFor(post(urlPathEqualTo("/payment-service/api/v1/payments"))
//                .willReturn(aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("{\"paymentId\": 1, \"isSuccess\": true}")));
//
//        stubFor(put(urlPathEqualTo("/payment-service/api/v1/payments/1"))
//                .willReturn(aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("{\"paymentId\": 1, \"isSuccess\": true}")));
//
//        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
//        String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiQ0VSVElGSUVEX1VTRVIiLCJlbWFpbCI6InppbWJvdGUwNDExQGd" +
//                "tYWlsLmNvbSIsImlhdCI6MTcyMDY5OTEzNywiZXhwIjoxNzIwNzAwMDM3fQ.ZvOicNw18UIFJVXFST3OrKGcoqZxXL0VAVehx" +
//                "v-ZZ0Su0lV7EWGcxITFEKH6xeG67ZDvBpoGm6yHHUqJDbM1Tw";
//        mockHttpServletRequest.setCookies(new MockCookie("accessToken", accessToken));
//
//        for (int i = 0; i < NUM_THREADS; i++) {
//            executorService.execute(() -> {
//                try {
//                    orderService.createOrderOfLimitedItem(
//                            new ReqLimitedOrderDto(1L, 10000), mockHttpServletRequest
//                    );
//                    successCount.getAndIncrement();
//                } catch (Exception e) {
//                    failCount.getAndIncrement();
//                } finally {
//                    doneSignal.countDown();
//                }
//            });
//        }
//        doneSignal.await();
//        executorService.shutdown();
//
//        //then
//        assertAll(
//                () -> assertThat(successCount.get()).isEqualTo(STOCK_QUANTITY),
//                () -> assertThat(failCount.get()).isEqualTo(990)
//        );
//    }
//}
