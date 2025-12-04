package com.example.fooddonation.service;

import com.example.fooddonation.entity.CartDTO;
import com.example.fooddonation.entity.CartItemDTO;
import com.example.fooddonation.entity.DonationDTO;
import com.example.fooddonation.entity.DonorDTO;
import com.example.fooddonation.entity.NgoDTO;
import com.example.fooddonation.repository.CartRepository;
import com.example.fooddonation.repository.CartItemRepository;
import com.example.fooddonation.repository.DonationRepository;
import com.example.fooddonation.repository.DonorRepository;
import com.example.fooddonation.repository.NgoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private NgoRepository ngoRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CartDTO getOrCreateCart(int donorId, int ngoId) {
        CartDTO cart = cartRepository.findPendingCartByDonor(donorId);
        if (cart != null) {
            // ensure items loaded
            cart.getCartItems().size();
            return cart;
        }

        DonorDTO donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        NgoDTO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        CartDTO newCart = new CartDTO();
        newCart.setDonor(donor);
        newCart.setNgo(ngo);

        return cartRepository.save(newCart);
    }

    public CartDTO addItem(int cartId, CartItemDTO item) {
        CartDTO cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        // ✅ FIX: Make sure the item has its NGO set
        // If item.getNgo() is null, use cart's NGO as fallback
        if (item.getNgo() == null) {
            item.setNgo(cart.getNgo());
        }
        
        item.setCart(cart);
        cart.getCartItems().add(item);
        return cartRepository.save(cart);
    }

    public CartDTO findCartByDonor(int donorId) {
        CartDTO cart = cartRepository.findPendingCartByDonor(donorId);
        if (cart == null) return null;
        cart.getCartItems().size();
        return cart;
    }

    @Transactional
    public void removeItem(int itemId) {
        // find and remove reference; orphanRemoval will delete
        CartItemDTO item = entityManager.find(CartItemDTO.class, itemId);
        if (item == null) return;
        CartDTO cart = item.getCart();
        cart.getCartItems().remove(item);
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(int cartId) {
        CartDTO cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public void checkout(int cartId) {
        CartDTO cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        for (CartItemDTO item : cart.getCartItems()) {
            DonationDTO d = new DonationDTO();
            d.setDonationType(item.getDonationType());
            d.setFoodName(item.getFoodName());
            d.setMealType(item.getMealType());
            d.setCategory(item.getCategory());
            d.setQuantity(item.getQuantity());
            d.setCity(item.getCity());
            d.setAmount(item.getAmount());
            d.setClothesType(item.getClothesType());
            d.setItemName(item.getItemName());
            d.setDonor(cart.getDonor());
            
            // ✅ FIX: Use the item's NGO instead of cart's NGO
            d.setNgo(item.getNgo());  // Changed from cart.getNgo()
            
            donationRepository.save(d);
        }

        cart.getCartItems().clear();
        cart.setStatus("CONFIRMED");
        cartRepository.save(cart);
    }
}
