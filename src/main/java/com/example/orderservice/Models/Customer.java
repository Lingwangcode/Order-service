package com.example.orderservice.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Full name missing")
    private String fullName;
    @Column (unique = true)
    @NotBlank(message = "Must not be blank")
    @Size(min = 9, max = 13, message = "Ssn must be unique and between 9 and 13 characters")
    private String ssn;
    @Embedded
    @NotNull(message = "Can not be empty")
    private Address address;
    @Email(message = "E-mail must be set properly")
    private String email;

    @ElementCollection
    private Set<Long> wishlist = new HashSet<>();

    public Long getId() {
        return id;
    }


}
