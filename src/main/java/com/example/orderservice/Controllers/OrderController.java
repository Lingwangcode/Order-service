package com.example.orderservice.Controllers;

import com.example.orderservice.Models.Customer;
import com.example.orderservice.Models.Item;
import com.example.orderservice.Models.Orders;
import com.example.orderservice.Repos.OrderRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    RetryTemplate retryTemplate;
    private final OrderRepo orderRepo;
    @Value("${customer-service.url}")
            private String customerServiceUrl;
    @Value("${item-service.url}")
            private String itemServiceUrl;

    OrderController(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }
    @GetMapping("/getAll")
    public List<Orders> getAllOrders() {
        return orderRepo.findAll();
    }

    @GetMapping("/getByCustomerId/{customerId}")
    public List<Orders> getOrdersByCustomerId(@PathVariable Long customerId) {

        return orderRepo.findByCustomerId(customerId);
    }
    @PostMapping(path = "/buy")
    @Retryable
    public ResponseEntity<Orders> addOrder(@RequestParam Long customerId, @RequestParam List<Long> itemIds) {

        List<String> result = new ArrayList<>();


            String customerUrl = customerServiceUrl + "/customers/getById/" + customerId;
            Customer customer = restTemplate.getForObject(customerUrl, Customer.class);
            Orders order = new Orders(LocalDate.now(), customer.getId());
            if (customer != null) {

                for (Long itemId : itemIds) {

                        String itemUrl = itemServiceUrl + "/items/getById/" + itemId;
                        Item item = restTemplate.getForObject(itemUrl, Item.class);

                        if (item != null) {
                            order.addToItemIds(itemId);
                            order.setSum(order.getSum() + item.getPrice());
                            result.add("Item " + itemId + " added successfully");
                        } else {
                            throw new EntityNotFoundException("Item not found with ID: " + itemId);
                        }
                }

                if (!order.getItemIds().isEmpty()) {
                    orderRepo.save(order);
                   // result.add("Order added");
                } else {
                    result.add("No items to buy. Order cancelled");
                }
            } else {
                throw new EntityNotFoundException("Customer not found with ID: " + customerId);
            }

        return ResponseEntity.ok(order);
    }

   /* private <T> T retryOnServiceError(Supplier<T> supplier){ //Hanterar återförsök

            return retryTemplate.execute(context -> {
                System.out.println("Performing retry...");
                return supplier.get();
            });

    }

    */

    @GetMapping("/getAllCustomers") //Endast för att testa ansluting
    public @ResponseBody Customer[] getCustomers() {

        return restTemplate.getForObject(customerServiceUrl + "/customers/getAll" , Customer[].class);
    }
}
