package com.example.orderservice.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.lang.Long;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDate date;
    private Long customerId;

    @ManyToMany
    @JoinTable
    private List<Item> items = new ArrayList<>();

    public Orders(LocalDate ld, Long customerId, List<Item> items) {
        this.date = ld;
        this.customerId = customerId;
        this.items = items;
    }

}
