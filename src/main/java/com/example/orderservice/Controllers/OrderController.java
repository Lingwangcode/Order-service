package com.example.orderservice.Controllers;

import com.example.orderservice.Models.Customer;
import com.example.orderservice.Models.Item;
import com.example.orderservice.Models.Orders;
import com.example.orderservice.Repos.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
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
    @Retryable(
            value = {HttpClientErrorException.class, HttpServerErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000) // 1 sekund försening mellan försök
    )
    public List<String> addOrder(@RequestParam Long customerId, @RequestParam List<Long> itemIds) {
        List<String> result = new ArrayList<>();//RETURNERAR EN LIST<STRING> FÖR ATT VISA VILKA ITEMS SOM KUNDE LÄGGAS TILL.
        try{
            String customerUrl = customerServiceUrl + "/customers/getById/" + customerId;
            Customer customer = retryOnServiceError(()-> restTemplate.getForObject(customerUrl, Customer.class));
            if (customer == null) { //FLYTTAT UPP OCH ÄNDRAT TILL == ISTÄLLET FÖR !=
                result.add("Customer not found, no order placed");
            } else {
                Orders order = new Orders(LocalDate.now(), customer.getId());   //SKAPAR UPP EN INSTANS AV 'ORDER'
                for (Long itemId : itemIds) {
                    try{
                        String itemUrl = itemServiceUrl + "/items/getById/" + itemId;
                        Item item = retryOnServiceError(()-> restTemplate.getForObject(itemUrl, Item.class)); //BORDE ISTÄLLET ANROPA EN ANNAN FUNKTION I 'ITEMS' SOM OCKSÅ UPPDATERAR ITEMS-DATABASEN
                        if (item != null) {
                            order.addToItemIds(itemId); //LÄGGA TILL I LISTAN AV ITEMIDS
                            order.setSum(order.getSum() + item.getPrice()); //LÄGGA PÅ VARANS PRIS TILL TOTALSUMMAN

                            result.add("Item " + itemId + " added successfully");   //LÄGGA TILL RESPONS PÅ ATT DET LYCKATS
                        } else {
                            result.add("Item " + itemId + " not found");
                        }
                    }catch (HttpServerErrorException | HttpClientErrorException e){
                        result.add("Failed to retrieve item " + itemId + ": " + e.getMessage());
                    }
                }
                if (!order.getItemIds().isEmpty()) {//OM DET KUNNAT ADDAS NÅGRA ITEMIDS TILL LISTAN
                    orderRepo.save(order);  //ORDERN SPARAS I DATABASEN
                    result.add("Order added");
                }else {
                    result.add("No items to buy. Order cancelled");
                }
            }

        }catch (HttpServerErrorException | HttpClientErrorException e){
            result.add("Failed to retrieve customer " + customerId + ": " + e.getMessage());
        }catch (Exception e){
            result.add("Faild to retrieve data: " + e.getMessage());
        }
        return result;
    }

    private <T> T retryOnServiceError(Supplier<T> supplier){ //Hanterar återförsök
        RetryTemplate retryTemplate = new RetryTemplate();
        try{
            return retryTemplate.execute(context -> supplier.get());
        }catch (Exception e){
            System.out.println("Something went wrong during retry: " + e.getMessage());
            throw e;
        }
    }

    @GetMapping("/getAllCustomers") //Endast för att testa ansluting
    public @ResponseBody Customer[] getCustomers() {

        return restTemplate.getForObject(customerServiceUrl + "/customers/getAll" , Customer[].class);
    }
}
