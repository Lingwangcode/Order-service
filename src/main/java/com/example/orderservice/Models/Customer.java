package com.example.orderservice.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    private Long id;
    private String fullName;
    private String ssn;
    @Embedded
    private Address address;
    private String email;

    @ElementCollection
    private Set<Long> wishlist = new HashSet<>();

    public Long getId() {
        return id;
    }


}
