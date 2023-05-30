package com.example.orderservice.Controllers;

import com.example.orderservice.Models.Customer;
import com.example.orderservice.Models.Item;
import com.example.orderservice.Models.Orders;
import com.example.orderservice.Repos.OrderRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
    public List<String> addOrder(@RequestParam Long customerId, @Valid @RequestParam List<Long> itemIds) {
        List<String> result = new ArrayList<>();    //RETURNERAR EN LIST<STRING> FÖR ATT VISA VILKA ITEMS SOM KUNDE LÄGGAS TILL.
        String customerUrl = customerServiceUrl + "/customers/getById/" + customerId;
        Customer customer = restTemplate.getForObject(customerUrl, Customer.class);
        if (customer == null) { //FLYTTAT UPP OCH ÄNDRAT TILL == ISTÄLLET FÖR !=
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        } else {
            Orders order = new Orders(LocalDate.now(), customer.getId());   //SKAPAR UPP EN INSTANS AV 'ORDER'
            for (Long itemId : itemIds) {
                String itemUrl = itemServiceUrl + "/items/getById/" + itemId;
                Item item = restTemplate.getForObject(itemUrl, Item.class); //BORDE ISTÄLLET ANROPA EN ANNAN FUNKTION I 'ITEMS' SOM OCKSÅ UPPDATERAR ITEMS-DATABASEN
                if (item != null) {
                    order.addToItemIds(itemId); //LÄGGA TILL I LISTAN AV ITEMIDS
                    order.setSum(order.getSum() + item.getPrice()); //LÄGGA PÅ VARANS PRIS TILL TOTALSUMMAN

                    result.add("Item " + itemId + " added successfully");   //LÄGGA TILL RESPONS PÅ ATT DET LYCKATS
                } else {
                    throw new EntityNotFoundException("Item not found with ID: " + itemId);
                }
            }
            if (!order.getItemIds().isEmpty()) {//OM DET KUNNAT ADDAS NÅGRA ITEMIDS TILL LISTAN
                orderRepo.save(order);  //ORDERN SPARAS I DATABASEN
                result.add("Order added");
            }else {
                result.add("No items to buy. Order cancelled");
            }
        }
        return result;
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
