package com.example.orderservice.Models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private LocalDate date;
    private Long customerId; //Får vara null ifall kund raderas från databasen
    @NotNull(message = "Order can not be given for free!")
    @Min(value = 0, message = "Price has to be above 0!")
    private int sum;

    @ElementCollection
    @NotEmpty(message = "An order needs items!")
    private List<Long> itemIds = new ArrayList<>();

    public Orders(LocalDate ld, Long customerId) {
        this.date = ld;
        this.customerId = customerId;
    }
    public void addToItemIds (Long itemId){
        itemIds.add(itemId);
    }

}
