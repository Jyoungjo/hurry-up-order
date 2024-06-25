package com.purchase.hanghae99.item;

import com.purchase.hanghae99.item.dto.create.ReqCreateItemDto;
import com.purchase.hanghae99.item.dto.create.ResCreateItemDto;
import com.purchase.hanghae99.item.dto.read.ResReadItemDto;
import com.purchase.hanghae99.item.dto.update.ReqUpdateItemDto;
import com.purchase.hanghae99.item.dto.update.ResUpdateItemDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ResCreateItemDto> createItem(@Valid @RequestBody ReqCreateItemDto req) {
        return ResponseEntity.status(CREATED).body(itemService.createItem(req));
    }

    @GetMapping
    public ResponseEntity<Page<ResReadItemDto>> readAllItems(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        return ResponseEntity.ok(itemService.readAllItems(page, size));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ResReadItemDto> readItem(
            @PathVariable("itemId") Long itemId
    ) {
        return ResponseEntity.ok(itemService.readItem(itemId));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ResUpdateItemDto> updateItem(
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody ReqUpdateItemDto req
    ) {
        return ResponseEntity.ok(itemService.updateItem(itemId, req));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("itemId") Long itemId
    ) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
