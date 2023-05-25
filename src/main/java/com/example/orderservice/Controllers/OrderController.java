package com.example.orderservice.Controllers;

import com.example.orderservice.Models.Customer;
import com.example.orderservice.Models.Item;
import com.example.orderservice.Models.Orders;
import com.example.orderservice.Repos.ItemRepo;
import com.example.orderservice.Repos.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;
    private final OrderRepo orderRepo;

    private final ItemRepo itemRepo;

    OrderController(OrderRepo orderRepo, RestTemplate restTemplate, ItemRepo itemRepo) {
        this.orderRepo = orderRepo;
        this.restTemplate = restTemplate;
        this.itemRepo = itemRepo;
    }
    @RequestMapping("/getAll")
    public List<Orders> getAllOrders() {
        return orderRepo.findAll();
    }

    @RequestMapping("/getByCustomerId/{customerId}")
    public List<Orders> getOrdersByCustomerId(@PathVariable Long customerId) {

        return orderRepo.findByCustomerId(customerId);
    }
    @PostMapping(path = "/buy")
    public String addOrder(@RequestParam Long customerId, @RequestParam List<Long> itemIds) {
        Customer customer = restTemplate.getForObject("http://Customers:8080/customers/getById/"+ customerId, Customer.class);
        List<Item> items = new ArrayList<>();
        for (Long itemId : itemIds) {
            Item item = restTemplate.getForObject("http://Items:8080/items/getById/" + itemId, Item.class);

            if(item != null){
               // item.setStock(1);
               items.add(item);
              // itemRepo.save(item);
            }
            else return "Item not found";
        }
        if (customer!=null){
            Orders order = new Orders(LocalDate.now(),customer.getId(),items);
            orderRepo.save(order);
            return "Order added";
        }
        else
            return "Customer not found";
    }

    @GetMapping("/getAllCustomers")
    public @ResponseBody Customer[] getCustomers() {

        return restTemplate.getForObject("http://Customers:8080/customers/getAll" , Customer[].class);
    }
}
