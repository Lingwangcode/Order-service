package com.example.orderservice.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String fullName;
   // @Column (unique = true)
    private String ssn;
    @Embedded
    private Address address;
    private String email;

    @ElementCollection
    private Set<Long> wishlist = new HashSet<>();

    public Long getId() {
        return id;
    }

/* public Customer(String fullName, String ssn, Address address, String email) {
        this.fullName = fullName;
        this.ssn = ssn;
        this.address = address;
        this.email = email;
    }

    public void addToWishlist (listItem item){
        wishlist.add(item);
    }

    public void removeFromWishlist (listItem item){
        wishlist.remove(item);
    }

    */

}
