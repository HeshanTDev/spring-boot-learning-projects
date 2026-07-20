package com.heshant.mapstruct.service;

import com.heshant.mapstruct.dto.OrderDTO;
import com.heshant.mapstruct.entity.Order;
import com.heshant.mapstruct.mapper.OrderMapper;
import com.heshant.mapstruct.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    public OrderDTO save(OrderDTO orderDTO){
        Order save = orderRepository.save(orderMapper.toEntity(orderDTO));

        return orderMapper.toDTO(save);
    }

    public OrderDTO findOrderById(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found!"));

        return orderMapper.toDTO(order);
    }

}
