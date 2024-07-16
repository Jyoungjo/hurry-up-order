package com.purchase.preorder.stock;

import com.purchase.preorder.common.RedisService;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.exception.ExceptionCode;
import com.purchase.preorder.item.Item;
import com.purchase.preorder.stock.dto.ReqStockDto;
import com.purchase.preorder.stock.dto.ResStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.preorder.exception.ExceptionCode.NOT_ENOUGH_STOCK;
import static com.purchase.preorder.exception.ExceptionCode.NOT_FOUND_STOCK;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {
    private static final String STOCK_KEY_PREFIX = "stock:";
    private final StockRepository stockRepository;
    private final RedisService redisService;

    @Override
    public void createStock(Item item, int quantity) {
        stockRepository.save(Stock.of(item, quantity));
    }

    @Override
    public void increaseStock(Long itemId, int quantity) {
        redisService.increment(STOCK_KEY_PREFIX + itemId, quantity);
        Stock stock = stockRepository.findByItemId(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
        stock.increaseQuantity(quantity);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void increaseStock(ReqStockDto req) {
        Stock stock = stockRepository.findByItemId(req.getItemId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
        stock.increaseQuantity(req.getQuantity());
        stockRepository.save(stock);
    }

    @Override
    public void decreaseStock(Long itemId, int quantity) {
        Long newQuantity = redisService.increment(STOCK_KEY_PREFIX + itemId, -quantity);
        if (newQuantity != null && newQuantity < 0) {
            redisService.increment(STOCK_KEY_PREFIX + itemId, quantity);
            throw new BusinessException(NOT_ENOUGH_STOCK);
        }
        Stock stock = stockRepository.findByItemId(itemId)
                        .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
        stock.decreaseQuantity(quantity);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void decreaseStock(ReqStockDto req) {
        Stock stock = stockRepository.findByItemId(req.getItemId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_STOCK));
        if (stock.getQuantity() < req.getQuantity()) {
            throw new BusinessException(NOT_ENOUGH_STOCK);
        }

        stock.decreaseQuantity(req.getQuantity());
        stockRepository.save(stock);
    }

    @Override
    public ResStockDto getStockQuantity(Long itemId) {
        String value = redisService.getValues(STOCK_KEY_PREFIX + itemId);
        if (value == null) {
            Stock stock = stockRepository.findByItemId(itemId)
                    .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_STOCK));
            redisService.setValues(STOCK_KEY_PREFIX + itemId, stock.getQuantity());
            return ResStockDto.of(stock.getQuantity());
        }

        return ResStockDto.of(Integer.parseInt(value));
    }
}
