package com.example.fooddonation.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddonation.entity.orderTransactionDetails;
import com.example.fooddonation.service.orderServices;


@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class paymentTransactionController {

    @Autowired
    private orderServices orderService;

    @PostMapping("/create-order")
    public orderTransactionDetails createOrder(@RequestBody AmountRequest request) {
        return orderService.orderCreateTransaction(request.getAmount());
    }
}

class AmountRequest {
    private int amount; // amount in paise

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}