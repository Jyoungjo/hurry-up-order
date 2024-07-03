package com.purchase.hanghae99_order.order;

import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_core.exception.ExceptionCode;
import com.purchase.hanghae99_order.order.dto.ReqOrderDto;
import com.purchase.hanghae99_order.order.dto.ResOrderDto;
import com.purchase.hanghae99_order.order_item.OrderItemService;
import com.purchase.hanghae99_order.user.User;
import com.purchase.hanghae99_order.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.purchase.hanghae99_core.util.AesUtils.aesCBCEncode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    @Override
    @Transactional
    public ResOrderDto createOrder(ReqOrderDto req, Authentication authentication) throws Exception {
        String emailOfConnectingUser = aesCBCEncode(authentication.getName());

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        // Order 객체 생성
        Order order = Order.of(user);
        orderRepository.save(order);

        // OrderItem 객체 생성 및 저장 로직
        orderItemService.createOrderItem(order, req.getOrderItemList());

        // Order save
        return ResOrderDto.fromEntity(orderRepository.save(order));
    }

    @Override
    public Page<ResOrderDto> readAllOrder(Authentication authentication, Integer page, Integer size) throws Exception {
        String emailOfConnectingUser = aesCBCEncode(authentication.getName());

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        Pageable pageable = PageRequest.of(page, size);
        List<ResOrderDto> orderDtoList = orderRepository.findAll()
                .stream()
                .filter(order -> order.getUser().equals(user))
                .map(ResOrderDto::fromEntity)
                .toList();

        return new PageImpl<>(orderDtoList, pageable, orderDtoList.size());
    }

    @Override
    public ResOrderDto readOrder(Authentication authentication, Long orderId) throws Exception {
        String emailOfConnectingUser = aesCBCEncode(authentication.getName());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER));

        checkMatchesUser(order.getUser().getEmail(), emailOfConnectingUser);

        return ResOrderDto.fromEntity(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Authentication authentication, Long orderId) throws Exception {
        String emailOfConnectingUser = aesCBCEncode(authentication.getName());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER));

        checkMatchesUser(order.getUser().getEmail(), emailOfConnectingUser);

        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Authentication authentication, Long orderId, Long itemId) throws Exception {
        String emailOfConnectingUser = aesCBCEncode(authentication.getName());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER));

        checkMatchesUser(order.getUser().getEmail(), emailOfConnectingUser);

        orderItemService.cancelOrder(order, itemId);
    }

    @Override
    @Transactional
    public void returnOrder(Authentication authentication, Long orderId, Long itemId) throws Exception {
        String emailOfConnectingUser = aesCBCEncode(authentication.getName());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER));

        checkMatchesUser(order.getUser().getEmail(), emailOfConnectingUser);

        orderItemService.requestReturnOrder(order, itemId);
    }

    private void checkMatchesUser(String userEmail, String emailOfConnectingUser) {
        if (!userEmail.equals(emailOfConnectingUser)) {
            throw new BusinessException(ExceptionCode.UNAUTHORIZED_ACCESS);
        }
    }
}
