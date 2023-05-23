package com.example.webbshopbackend1.Repos;

import com.example.webbshopbackend1.Models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Orders, Long> {

    List<Orders> findByCustomerId(Long customerId);

}

