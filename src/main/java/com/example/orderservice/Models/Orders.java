package com.example.orderservice.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.lang.Long;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDate date;

    @ManyToOne
    @JoinColumn
    private Customer customer;
    @ManyToMany
    @JoinTable
    private List<Item> items = new ArrayList<>();
    public Orders(LocalDate ld, Customer customer, List<Item> items) {
        this.date = ld;
        this.customer = customer;
        this.items = items;
    }
}
