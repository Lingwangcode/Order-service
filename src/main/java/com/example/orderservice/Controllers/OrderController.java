package com.example.orderservice.Controllers;

import com.example.orderservice.Models.Customer;
import com.example.orderservice.Models.Item;
import com.example.orderservice.Models.Orders;
import com.example.orderservice.Repos.OrderRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@RestController
@Validated
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    private final OrderRepo orderRepo;
    @Value("${customer-service.url}")
            private String customerServiceUrl;
    @Value("${item-service.url}")
            private String itemServiceUrl;

    OrderController(OrderRepo orderRepo, RestTemplate restTemplate) {
        this.orderRepo = orderRepo;
        this.restTemplate = restTemplate;
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
    @Retryable
    public List<String> addOrder(@RequestParam Long customerId, @RequestParam List<Long> itemIds) {
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
                if (item == null) {
                    result.add("Item not found with ID: " + itemId);
                 //   throw new EntityNotFoundException("Item not found with ID: " + itemId);
                } else {
                    order.addToItemIds(itemId); //LÄGGA TILL I LISTAN AV ITEMIDS
                    order.setSum(order.getSum() + item.getPrice()); //LÄGGA PÅ VARANS PRIS TILL TOTALSUMMAN

                    result.add("Item " + itemId + " added successfully");   //LÄGGA TILL RESPONS PÅ ATT DET LYCKATS

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
    @PostMapping(path = "/buy/{customerId}")
    public List<String> addOrderPV(@PathVariable Long customerId, @RequestBody List<Long> itemIds) {
        List<String> result = new ArrayList<>();
        Customer customer = restTemplate.getForObject("http://Customers:8080/customers/getById/" + customerId, Customer.class);
        if (customer == null) {
            result.add("Customer not found, no order placed");
        } else {
            //List<Item> items = new ArrayList<>();
            Orders order = new Orders(LocalDate.now(), customer.getId());
            for (Long itemId : itemIds) {
                Item item = restTemplate.getForObject("http://Items:8080/items/getById/" + itemId, Item.class);
                if (item != null) {
                    order.addToItemIds(itemId);
                    order.setSum(order.getSum() + item.getPrice());
                    //items.add(item);
                    result.add("Item " + itemId + " added successfully");
                } else {
                    result.add("Item " + itemId + " not found");
                }
            }
            if (!order.getItemIds().isEmpty()) {
                orderRepo.save(order);
                result.add("Order added");
            }else {
                result.add("No items to buy. Order cancelled");
            }
        }
        return result;
    }

    @GetMapping("/getAllCustomers") //For test only
    public @ResponseBody Customer[] getCustomers() {

        return restTemplate.getForObject(customerServiceUrl + "/customers/getAll" , Customer[].class);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
