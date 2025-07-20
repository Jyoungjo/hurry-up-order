package com.purchase.preorder.payment_service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/payment-service")
public class NicePaymentController {

    @PostMapping("/nice-return")
    public void handleNiceReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        // 1. JSON으로 직렬화
        String json = new ObjectMapper().writeValueAsString(params);

        // 2. JS로 리다이렉트 HTML 작성
        String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>결제 결과 처리 중...</title>
                    </head>
                    <body>
                        <p>결제 승인 중입니다...</p>
                        <script>
                            (async () => {
                                const data = %s;

                                try {
                                    const response = await fetch('/payment-service/api/v1/payments/nice/confirm', {
                                        method: 'POST',
                                        headers: {
                                            'Content-Type': 'application/json'
                                        },
                                        body: JSON.stringify({
                                            tid: data.tid,
                                            orderId: data.orderId,
                                            amount: Number(data.amount)
                                        })
                                    });

                                    if (!response.ok) throw new Error('승인 실패');

                                    const result = await response.json();
                                    const encoded = encodeURIComponent(JSON.stringify(result));
                                    window.location.href = '/payment-service/success2.html?result=' + encoded;

                                } catch (error) {
                                    console.error(error);
                                    window.location.href = '/payment-service/fail2.html';
                                }
                            })();
                        </script>
                    </body>
                    </html>
                """.formatted(json);

        // 3. HTML 반환
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(html);
    }
}
