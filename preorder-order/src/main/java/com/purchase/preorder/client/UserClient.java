package com.purchase.preorder.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${feign.client.config.user-service.url}")
public interface UserClient {
    @GetMapping("/user-service/api/v1/internal/users")
    UserResponse getUserByEmail(@RequestParam("email") String email);
}
