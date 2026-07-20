package com.heshant.mapstruct.mapper;

import com.heshant.mapstruct.dto.OrderDTO;
import com.heshant.mapstruct.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "orderAmount", target = "amount")
    @Mapping(source = "orderDate", target = "date", dateFormat = "yyyy-MMM-dd")
    Order toEntity(OrderDTO orderDTO);

    @Mapping(target = "orderAmount", source = "amount")
    @Mapping(target = "orderDate", source = "date", dateFormat = "yyyy-MMM-dd")
    OrderDTO toDTO(Order order);
}
