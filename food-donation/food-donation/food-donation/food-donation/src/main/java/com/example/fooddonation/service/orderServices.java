package com.example.fooddonation.service;




import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.example.fooddonation.entity.orderTransactionDetails;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class orderServices {

    private static final String KEY = "rzp_test_RjzWLkPc2oFYv7";     // your Razorpay key
    private static final String KEY_SECRET = "QjeRSBi2t4wHD11ue7I1iVi6"; // your secret
    private static final String CURRENCY = "INR";

    public orderTransactionDetails orderCreateTransaction(int amount) {
        try {
            JSONObject json = new JSONObject();
            json.put("amount", amount);
            json.put("currency", CURRENCY);

            RazorpayClient client = new RazorpayClient(KEY, KEY_SECRET);
            Order order = client.orders.create(json);

            System.out.println("Order: "+order);
            return new orderTransactionDetails(
                    order.get("id"),
                    order.get("currency"),
                    order.get("amount"),
                    KEY
                    
                    
            );

            
        } catch (Exception e) {
            System.out.println("Razorpay error: " + e.getMessage());
            return null;
        }
    }
}