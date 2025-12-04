package com.example.fooddonation.controller;

import com.example.fooddonation.entity.CartDTO;
import com.example.fooddonation.entity.CartItemDTO;
import com.example.fooddonation.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/create/{donorId}/{ngoId}")
    public ResponseEntity<?> getOrCreate(@PathVariable int donorId, @PathVariable int ngoId) {
        try {
            CartDTO cart = cartService.getOrCreateCart(donorId, ngoId);
            return ResponseEntity.ok(cart);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/add-item/{cartId}")
    public ResponseEntity<?> addItem(@PathVariable int cartId, @RequestBody CartItemDTO item) {
        try {
            CartDTO cart = cartService.addItem(cartId, item);
            return ResponseEntity.ok(cart);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/find-by-donor/{donorId}")
    public ResponseEntity<?> findCart(@PathVariable int donorId) {
        CartDTO cart = cartService.findCartByDonor(donorId);
        if (cart == null) return ResponseEntity.status(404).body(java.util.Map.of("error", "Cart not found"));
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove-item/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable int itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.ok(java.util.Map.of("message", "Item removed"));
    }

    @DeleteMapping("/clear/{cartId}")
    public ResponseEntity<?> clear(@PathVariable int cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok(java.util.Map.of("message", "Cart cleared"));
    }

    @PostMapping("/checkout/{cartId}")
    public ResponseEntity<?> checkout(@PathVariable int cartId) {
        cartService.checkout(cartId);
        return ResponseEntity.ok(java.util.Map.of("message", "Checkout complete"));
    }
}
