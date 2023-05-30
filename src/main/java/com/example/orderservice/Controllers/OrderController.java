package com.example.orderservice.Controllers;

import com.example.orderservice.Models.Customer;
import com.example.orderservice.Models.Item;
import com.example.orderservice.Models.Orders;
import com.example.orderservice.Repos.OrderRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public List<String> addOrder(@RequestParam Long customerId, @Valid @RequestParam List<Long> itemIds) {
        List<String> result = new ArrayList<>();    //RETURNERAR EN LIST<STRING> FÖR ATT VISA VILKA ITEMS SOM KUNDE LÄGGAS TILL.
        String customerUrl = customerServiceUrl + "/customers/getById/" + customerId;
        Customer customer = restTemplate.getForObject(customerUrl, Customer.class);
        if (customer == null) { //FLYTTAT UPP OCH ÄNDRAT TILL == ISTÄLLET FÖR !=
            result.add("Customer not found, no order placed");
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
                    result.add("Item " + itemId + " not found");

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
