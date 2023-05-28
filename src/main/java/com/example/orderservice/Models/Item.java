package com.example.orderservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @JsonIgnore
    private Long id;
    @NotBlank(message = "Must not be blank")
    @Size(min = 1 , max = 100, message = "Item name must be between 1 and 100 characters long")
    private String name;
    @Min(value = 1, message = "Item is not supposed to be given free!")
    @NotNull(message = "Must have a set price")
    private int price;
    @NotNull(message = "Must have a set stock")
    @JsonIgnore
    private int stock;
}
