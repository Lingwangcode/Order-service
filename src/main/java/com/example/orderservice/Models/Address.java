package com.example.orderservice.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {
    @NotBlank(message = "Street must be set")
    private String street;
    @NotBlank(message = "Must not be blank")
    @Size(min = 5, max = 5, message = "Zip code must contain 5 digits")
    private String zipCode;
    @NotBlank(message = "Postal address must be set")
    private String postalAddress;
    @NotBlank(message = "Country must be set")
    private String Country;
}
