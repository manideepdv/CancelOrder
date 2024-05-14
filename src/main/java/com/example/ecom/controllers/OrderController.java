package com.example.ecom.controllers;

import com.example.ecom.dtos.CancelOrderRequestDto;
import com.example.ecom.dtos.CancelOrderResponseDto;
import com.example.ecom.dtos.ResponseStatus;
import com.example.ecom.exceptions.OrderCannotBeCancelledException;
import com.example.ecom.exceptions.OrderDoesNotBelongToUserException;
import com.example.ecom.exceptions.OrderNotFoundException;
import com.example.ecom.exceptions.UserNotFoundException;
import com.example.ecom.models.Order;
import com.example.ecom.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public CancelOrderResponseDto cancelOrder(CancelOrderRequestDto cancelOrderRequestDto) {
        CancelOrderResponseDto responseDto = new CancelOrderResponseDto();
        try {
            Order order = orderService.cancelOrder(cancelOrderRequestDto.getOrderId(), cancelOrderRequestDto.getUserId());
            responseDto.setOrder(order);
            responseDto.setStatus(ResponseStatus.SUCCESS);
        } catch (UserNotFoundException | OrderCannotBeCancelledException | OrderDoesNotBelongToUserException |
                 OrderNotFoundException e) {
            System.out.println(e.getMessage());
            responseDto.setStatus(ResponseStatus.FAILURE);
        }
        return responseDto;
    }

}
