package com.purchase.preorder.order;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.exception.ExceptionCode;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import com.purchase.preorder.client.UserClient;
import com.purchase.preorder.client.UserResponse;
import com.purchase.preorder.order.dto.ReqOrderDto;
import com.purchase.preorder.order.dto.ResOrderDto;
import com.purchase.preorder.order_item.OrderItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final UserClient userClient;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    @Override
    @Transactional
    public ResOrderDto createOrder(ReqOrderDto req, HttpServletRequest request) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        // Order 객체 생성
        Order order = Order.of(user.getId());
        orderRepository.save(order);

        // OrderItem 객체 생성 및 저장 로직
        orderItemService.createOrderItem(order, req.getOrderItemList());

        // Order save
        return ResOrderDto.fromEntity(orderRepository.save(order));
    }

    @Override
    public Page<ResOrderDto> readAllOrder(HttpServletRequest request, Integer page, Integer size) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Pageable pageable = PageRequest.of(page, size);
        List<ResOrderDto> orderDtoList = orderRepository.findAll()
                .stream()
                .filter(order -> order.getUserId().equals(user.getId()))
                .map(ResOrderDto::fromEntity)
                .toList();

        return new PageImpl<>(orderDtoList, pageable, orderDtoList.size());
    }

    @Override
    public ResOrderDto readOrder(HttpServletRequest request, Long orderId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER));

        checkMatchesUser(user.getId(), order.getUserId());

        return ResOrderDto.fromEntity(order);
    }

    @Override
    @Transactional
    public void deleteOrder(HttpServletRequest request, Long orderId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER));

        checkMatchesUser(user.getId(), order.getUserId());

        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public void cancelOrder(HttpServletRequest request, Long orderId, Long itemId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER));

        checkMatchesUser(user.getId(), order.getUserId());

        orderItemService.cancelOrder(order, itemId);
    }

    @Override
    @Transactional
    public void returnOrder(HttpServletRequest request, Long orderId, Long itemId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_ORDER));

        checkMatchesUser(user.getId(), order.getUserId());

        orderItemService.requestReturnOrder(order, itemId);
    }

    private void checkMatchesUser(Long userId, Long userIdOfConnectingUser) {
        if (!userId.equals(userIdOfConnectingUser)) {
            throw new BusinessException(ExceptionCode.UNAUTHORIZED_ACCESS);
        }
    }

    private String getEmailOfAuthenticatedUser(HttpServletRequest request) throws Exception {
        String accessToken = CustomCookieManager.getCookie(request, CustomCookieManager.ACCESS_TOKEN);
        return AesUtils.aesCBCEncode(JwtParser.getEmail(accessToken));
    }
}
