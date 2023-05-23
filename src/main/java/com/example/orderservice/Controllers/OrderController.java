package com.example.orderservice.Controllers;

import com.example.orderservice.Models.Customer;
import com.example.orderservice.Models.Item;
import com.example.orderservice.Models.Orders;
import com.example.orderservice.Repos.OrderRepo;

import com.example.orderservice.Services.OrderService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepo orderRepo;
    private final OrderService orderService;

    OrderController(OrderRepo orderRepo, OrderService orderService) {
        this.orderRepo = orderRepo;
        this.orderService = orderService;
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
        Customer customer = orderService.getCustomerById(customerId);
        List<Item> items = new ArrayList<>();
        for (Long itemId : itemIds) {
            Item item = orderService.getItemById(itemId);
            items.add(item);
        }
        Orders order = new Orders(LocalDate.now(),customer, items);
        orderRepo.save(order);
        return "Order added";
    }
}
