package com.purchase.preorder.stock;

import com.purchase.preorder.item_service.config.JpaConfig;
import com.purchase.preorder.item.Item;
import com.purchase.preorder.item.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public class StockRepositoryTest {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;

    private Stock stock() {
        return Stock.builder()
                .item(item)
                .quantity(5000)
                .build();
    }

    @BeforeEach
    void init() {
        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("제품명")
                .price(15000)
                .description("이 제품에 대한 설명 입니다.")
                .deletedAt(null)
                .build());
    }

    // CREATE
    @DisplayName("재고 생성 성공")
    @Test
    void createStock() {
        // given
        Stock stock = stock();

        // when
        Stock savedStock = stockRepository.save(stock);

        // then
        assertThat(savedStock.getQuantity()).isEqualTo(stock.getQuantity());
    }

    // READ
    @DisplayName("재고 확인 성공")
    @Test
    void readStock() {
        // given
        Stock savedStock = stockRepository.save(stock());

        // when
        Optional<Stock> foundStock = stockRepository.findById(savedStock.getId());

        // then
        Assertions.assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getId()).isEqualTo(savedStock.getId());
    }

    // UPDATE
    @DisplayName("재고 변경 성공")
    @Test
    void updateStock() {
        // given
        Stock savedStock = stockRepository.save(stock());
        int quantity = savedStock.getQuantity();

        // when
        savedStock.increaseQuantity(1000);
        Stock updatedStock = stockRepository.save(savedStock);

        // then
        assertThat(updatedStock.getQuantity()).isEqualTo(quantity + 1000);
    }
}
