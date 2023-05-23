package com.example.orderservice.Services;

import com.example.orderservice.Models.Customer;
import com.example.orderservice.Models.Item;
import com.example.orderservice.Repos.CustomerRepo;
import com.example.orderservice.Repos.ItemRepo;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    @Autowired
    private RestTemplate restTemplate;
    private String customerServiceUrl;
    private String itemServiceUrl;
    private final CustomerRepo customerRepo;
    private final ItemRepo itemRepo;

    public OrderService(CustomerRepo customerRepo, ItemRepo itemRepo) {
        restTemplate = new RestTemplate();
        customerServiceUrl = "http://customer-service/customer/{customerId}";
        itemServiceUrl = "http://item-service/item/{itemId}";
        this.customerRepo = customerRepo;
        this.itemRepo = itemRepo;
    }

    public Customer getCustomerById(Long customerId) {
        //String url = customerServiceUrl.replace("{customerId}", String.valueOf(customerId));
       // return restTemplate.getForObject(url, Customer.class);
        Customer testCustomer = new Customer("Test Customer1", "test123");
        customerRepo.save(testCustomer);
        Customer test = customerRepo.findById(customerId).orElse(null);
        return test;
    }

    public Item getItemById(Long itemId) {
        //String url = itemServiceUrl.replace("{itemId}", String.valueOf(itemId));
        //return restTemplate.getForObject(url, Item.class);
        Item testItem = new Item("Test Item", 123, 1);
        itemRepo.save(testItem);
        Item test = itemRepo.findById(itemId).orElse(null);
        return test;
    }
}
