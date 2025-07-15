package com.purchase.preorder.item_service.item;

import com.purchase.preorder.item_service.item.dto.create.ReqCreateItemDto;
import com.purchase.preorder.item_service.item.dto.create.ResCreateItemDto;
import com.purchase.preorder.item_service.item.dto.read.ResReadItemDto;
import com.purchase.preorder.item_service.item.dto.update.ReqUpdateItemDto;
import com.purchase.preorder.item_service.item.dto.update.ResUpdateItemDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-service/api/v1")
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/items")
    public ResponseEntity<ResCreateItemDto> createItem(@Valid @RequestBody ReqCreateItemDto req) {
        return ResponseEntity.status(CREATED).body(itemService.createItem(req));
    }

    @GetMapping("/items")
    public ResponseEntity<Page<ResReadItemDto>> readAllItems(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        return ResponseEntity.ok(itemService.readAllItems(page, size));
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<ResReadItemDto> readItem(
            @PathVariable("itemId") Long itemId
    ) {
        return ResponseEntity.ok(itemService.readItem(itemId));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ResUpdateItemDto> updateItem(
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody ReqUpdateItemDto req
    ) {
        return ResponseEntity.ok(itemService.updateItem(itemId, req));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("itemId") Long itemId
    ) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // TODO 테스트 진행
    @GetMapping("/internal/items/{itemId}")
    public ResponseEntity<ResReadItemDto> findItem(
            @PathVariable("itemId") Long itemId
    ) {
        return ResponseEntity.ok(itemService.findItem(itemId));
    }
}
