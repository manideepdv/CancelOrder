package com.example.ecom.services;

import com.example.ecom.exceptions.OrderCannotBeCancelledException;
import com.example.ecom.exceptions.OrderDoesNotBelongToUserException;
import com.example.ecom.exceptions.OrderNotFoundException;
import com.example.ecom.exceptions.UserNotFoundException;
import com.example.ecom.models.*;
import com.example.ecom.repositories.InventoryRepository;
import com.example.ecom.repositories.OrderDetailRepository;
import com.example.ecom.repositories.OrderRepository;
import com.example.ecom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderDetailRepository orderDetailRepository, InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.inventoryRepository = inventoryRepository;
    }
    
    @Override
    public Order cancelOrder(int orderId, int userId) throws UserNotFoundException, OrderNotFoundException, OrderDoesNotBelongToUserException, OrderCannotBeCancelledException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User Not Found");
        }
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new OrderNotFoundException("Order Not Found");
        }
        Order order = optionalOrder.get();
        if (order.getUser().getId() != userId) {
            throw new OrderDoesNotBelongToUserException("Order Not Belong to User");
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderCannotBeCancelledException("Order Cannot be Cancelled");
        }
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        try{
            return cancellingOrder(orderDetails, order);
        } catch (OrderNotFoundException orderNotFoundException){
            System.out.println("While cancelling, order not found!!");
        }

        return order;
    }


    public Order cancellingOrder(List<OrderDetail> orderDetails, Order userOrder) throws OrderNotFoundException {
        for (OrderDetail od : orderDetails) {
            Product cancelledProduct = od.getProduct();
            int cancelledQuantity = od.getQuantity();

            synchronized(cancelledProduct){
                Optional<Inventory> inventoryOptional = inventoryRepository.findByProduct(cancelledProduct);
                if (inventoryOptional.isEmpty()) {
                    return userOrder;
                }
                Inventory updatedInventory = inventoryOptional.get();
                updatedInventory.setQuantity(updatedInventory.getQuantity() + cancelledQuantity);
                inventoryRepository.save(updatedInventory);
            }
        }

        //Marking order status as cancelled
        userOrder.setOrderStatus(OrderStatus.CANCELLED);

        return orderRepository.save(userOrder);

    }

}
