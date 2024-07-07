package com.purchase.preorder.item;

import com.purchase.preorder.config.JpaConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    private Item item() {
        return Item.builder()
                .id(1L)
                .name("제품명")
                .description("이 제품에 대한 설명 입니다.")
                .price(150000)
                .openTime(LocalDateTime.now())
                .isReserved(false)
                .deletedAt(null)
                .build();
    }

    // CREATE
    @DisplayName("상품 추가 성공")
    @Test
    void addItem() {
        // given
        Item item = item();

        // when
        Item savedItem = itemRepository.save(item);

        // then
        assertThat(savedItem.getName()).isEqualTo(item.getName());
    }

    // READ ALL
    @DisplayName("상품 목록 조회 성공")
    @Test
    void readAllItems() {
        // given
        itemRepository.save(item());

        // when
        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> foundItemPage = itemRepository.findAll(pageable);

        // then
        Assertions.assertThat(foundItemPage).hasSize(1);
    }
    
    // READ ONE
    @DisplayName("상품 개별 조회 성공")
    @Test
    void readItem() {
        // given
        Item savedItem = itemRepository.save(item());

        // when
        Optional<Item> foundItem = itemRepository.findById(savedItem.getId());
        
        // then
        Assertions.assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getName()).isEqualTo(savedItem.getName());
    }

    // UPDATE
    @DisplayName("상품 정보 변경")
    @Test
    void updateItem() {
        // given
        Item savedItem = itemRepository.save(item());

        // when
        String originalName = savedItem.getName();
        String originalDescription = savedItem.getDescription();
        Integer originalPrice = savedItem.getPrice();

        savedItem.updateInfo("제품명2", "이 제품에 대한 설명 입니다!!!", 200000);
        Item updatedItem = itemRepository.save(savedItem);

        // then
        assertThat(updatedItem.getName()).isNotEqualTo(originalName);
        assertThat(updatedItem.getDescription()).isNotEqualTo(originalDescription);
        assertThat(updatedItem.getPrice()).isNotEqualTo(originalPrice);
    }
    
    // DELETE
    @DisplayName("상품 삭제")
    @Test
    void deleteItem() {
        // given
        Item savedItem = itemRepository.save(item());
        
        // when
        itemRepository.delete(savedItem);
        
        // then
        assertThat(itemRepository.count()).isZero();
    }
}
