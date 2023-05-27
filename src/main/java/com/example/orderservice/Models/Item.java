package com.example.orderservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    private Long id;
    private String name;
    private int price;
    @JsonIgnore
    private int stock;

}
