package com.example.ecom.repositories;

import com.example.ecom.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Override
    Optional<User> findById(Integer integer);
}
