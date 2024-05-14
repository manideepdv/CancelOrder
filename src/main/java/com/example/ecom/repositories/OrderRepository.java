package com.example.ecom.repositories;

import com.example.ecom.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Override
    Optional<Order> findById(Integer integer);
}
