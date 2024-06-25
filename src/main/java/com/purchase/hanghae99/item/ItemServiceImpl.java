package com.purchase.hanghae99.item;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.item.dto.create.ReqCreateItemDto;
import com.purchase.hanghae99.item.dto.create.ResCreateItemDto;
import com.purchase.hanghae99.item.dto.read.ResReadItemDto;
import com.purchase.hanghae99.item.dto.update.ReqUpdateItemDto;
import com.purchase.hanghae99.item.dto.update.ResUpdateItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ResCreateItemDto createItem(ReqCreateItemDto req) {
        Item item = req.toEntity();
        return ResCreateItemDto.fromEntity(itemRepository.save(item));
    }

    @Override
    public Page<ResReadItemDto> readAllItems(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findAll(pageable).map(ResReadItemDto::fromEntity);
    }

    @Override
    public ResReadItemDto readItem(Long itemId) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
        return ResReadItemDto.fromEntity(savedItem);
    }

    @Override
    @Transactional
    public ResUpdateItemDto updateItem(Long itemId, ReqUpdateItemDto req) {
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));
        savedItem.updateInfo(req.getName(), req.getDescription(), req.getPrice());
        return ResUpdateItemDto.fromEntity(itemRepository.save(savedItem));
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new BusinessException(NOT_FOUND_ITEM);
        }

        itemRepository.deleteById(itemId);
    }
}
