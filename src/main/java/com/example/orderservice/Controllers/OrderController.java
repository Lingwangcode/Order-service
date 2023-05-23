package com.example.webbshopbackend1.Controllers;

import com.example.webbshopbackend1.Models.Customer;
import com.example.webbshopbackend1.Models.Item;
import com.example.webbshopbackend1.Models.Orders;
import com.example.webbshopbackend1.Repos.CustomerRepo;
import com.example.webbshopbackend1.Repos.ItemRepo;
import com.example.webbshopbackend1.Repos.OrderRepo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;
    private final ItemRepo itemRepo;

    OrderController(OrderRepo orderRepo, CustomerRepo customerRepo, ItemRepo itemRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
        this.itemRepo = itemRepo;
    }

    @RequestMapping("/getAll")
    public List<Orders> getAllOrders() {
        return orderRepo.findAll();
    }

    @RequestMapping("/getByCustomerId/{customerId}")
    public List<Orders> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<Orders> orders = orderRepo.findAll();
        List<Orders> customerOrders = new ArrayList<>();
        for (Orders order : orders) {
            if (order.getCustomer().getId() == customerId) {
                customerOrders.add(order);
            }
        }
        return customerOrders;
    }

    @PostMapping(path = "/buy")
    //curl -X POST -H "Content-Type: application/json" "http://localhost:8080/orders/buy?customerId=1&itemIds=2&itemIds=3"
    public String addOrder(@RequestParam Long customerId, @RequestParam List<Long> itemIds) {
        List<Item> items = new ArrayList<>();
        for (Long itemId : itemIds) {            //måste gå via en for-loop för att kunna lägga till flera av samma id i samma order
            Item item = itemRepo.findById(itemId).orElse(null);
            if (item != null && item.getStock() > 0) {
                items.add(item);
                item.setStock(item.getStock() - 1);
                itemRepo.save(item);
            } else {      //else sats för att breaka metoden att köra vidare vid fel itemid
                return "Order failed, item id /ids not found";
            }
        }
        Customer customer = customerRepo.findById(customerId).orElse(null); //orElse(null) krävs för att inte få 500-fel om obefintligt ID anges
        if (items != null && customer != null && customer.getName() != null) {
            orderRepo.save(new Orders(LocalDate.now(), customer, items));
            return "Order added";
        } else {
            return "Order failed, customer id not found";
        }
    }
}
