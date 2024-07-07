package com.purchase.preorder;

import com.purchase.preorder.client.ItemClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-service/error")
@RequiredArgsConstructor
public class ErrorTestController {
    private final ItemClient itemClient;

    @GetMapping("/case1")
    public ResponseEntity<String> getCase1() {
        return ResponseEntity.ok(itemClient.case1());
    }

    @GetMapping("/case2")
    public ResponseEntity<String> getCase2() {
        return ResponseEntity.ok(itemClient.case2());
    }

    @GetMapping("/case3")
    public ResponseEntity<String> getCase3() {
        return ResponseEntity.ok(itemClient.case3());
    }
}
