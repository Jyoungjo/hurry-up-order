package com.purchase.preorder.stock;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.exception.ExceptionCode;
import com.purchase.preorder.stock.dto.ReqStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    @Override
    public void increaseStock(Long itemId, int quantity) {
        Stock stock = stockRepository.findByItemId(itemId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_STOCK));
        stock.increaseQuantity(quantity);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void increaseStock(ReqStockDto req) {
        Stock stock = stockRepository.findByItemId(req.getItemId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_STOCK));
        stock.increaseQuantity(req.getQuantity());
        stockRepository.save(stock);
    }

    @Override
    public void decreaseStock(Long itemId, int quantity) {
        Stock stock = stockRepository.findByItemId(itemId)
                        .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_STOCK));
        if (stock.getQuantity() < quantity) {
            throw new BusinessException(ExceptionCode.NOT_ENOUGH_STOCK);
        }

        stock.decreaseQuantity(quantity);
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void decreaseStock(ReqStockDto req) {
        Stock stock = stockRepository.findByItemId(req.getItemId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_STOCK));
        if (stock.getQuantity() < req.getQuantity()) {
            throw new BusinessException(ExceptionCode.NOT_ENOUGH_STOCK);
        }

        stock.decreaseQuantity(req.getQuantity());
        stockRepository.save(stock);
    }

    @Override
    public int getStockQuantity(Long itemId) {
        Stock stock = stockRepository.findByItemId(itemId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_STOCK));
        return stock.getQuantity();
    }
}
