package com.example.orderservice.Repos;

import com.example.orderservice.Models.Orders;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Orders, Long> {
    List<Orders> findByCustomerId(Long customerId);
}

