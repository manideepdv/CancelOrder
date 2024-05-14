package com.example.ecom.repositories;

import com.example.ecom.models.Order;
import com.example.ecom.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Override
    Optional<OrderDetail> findById(Integer integer);

    List<OrderDetail> findByOrder(Order order);
}
