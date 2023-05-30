package com.example.orderservice.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    //    @Pattern(regexp = "\\d+", message = "Zipcode can only contain digits")
//    @Size(min = 5, max = 5, message = "Zip code must contain exactly 5 digits")
    @NotBlank(message = "Zip code must be set")//lite onödigt kan man tycka
    @Pattern(regexp = "\\d{5}", message = "Zip code must consist of exactly 5 digits and nothing else") //exakt 5 tecken lång, enbart siffror
    private String zipCode;
    @NotBlank(message = "Postal address must be set")
    private String postalAddress;
    @NotBlank(message = "Country must be set")
    private String Country;
}
