package com.example.orderservice.Repos;

import com.example.orderservice.Models.Customer;


import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, Long> {

   // Customer findByName(String testCustomer);
}
