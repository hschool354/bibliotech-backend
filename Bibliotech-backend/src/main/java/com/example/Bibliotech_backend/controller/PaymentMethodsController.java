package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.model.PaymentMethod;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.service.AuthService;
import com.example.Bibliotech_backend.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodsController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private AuthService authService;

    /**
     * Get all payment methods for the authenticated user
     *
     * @return List of payment methods
     */
    @GetMapping
    public ResponseEntity<List<PaymentMethod>> getUserPaymentMethods() {
        Integer userId = getCurrentUserId();
        List<PaymentMethod> paymentMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        return ResponseEntity.ok(paymentMethods);
    }

    /**
     * Add a new payment method for the authenticated user
     *
     * @param paymentMethod The payment method to add
     * @return The saved payment method
     */
    @PostMapping
    public ResponseEntity<PaymentMethod> addPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        Integer userId = getCurrentUserId();
        // Set the user ID from the authenticated user
        paymentMethod.setUserId(userId);

        PaymentMethod savedMethod = paymentMethodService.addPaymentMethod(paymentMethod);
        return new ResponseEntity<>(savedMethod, HttpStatus.CREATED);
    }

    /**
     * Update an existing payment method
     *
     * @param methodId ID of the payment method to update
     * @param paymentMethod Updated payment method details
     * @return The updated payment method
     */
    @PutMapping("/{methodId}")
    public ResponseEntity<PaymentMethod> updatePaymentMethod(
            @PathVariable Integer methodId,
            @RequestBody PaymentMethod paymentMethod) {

        Integer userId = getCurrentUserId();

        // Verify the payment method belongs to the current user
        List<PaymentMethod> userMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        boolean methodBelongsToUser = userMethods.stream()
                .anyMatch(method -> method.getPaymentMethodId().equals(methodId));

        if (!methodBelongsToUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Set the ID and user ID
        paymentMethod.setPaymentMethodId(methodId);
        paymentMethod.setUserId(userId);

        PaymentMethod updatedMethod = paymentMethodService.updatePaymentMethod(paymentMethod);
        return ResponseEntity.ok(updatedMethod);
    }

    /**
     * Delete a payment method
     *
     * @param methodId ID of the payment method to delete
     * @return No content response
     */
    @DeleteMapping("/{methodId}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Integer methodId) {
        Integer userId = getCurrentUserId();

        // Verify the payment method belongs to the current user
        List<PaymentMethod> userMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        boolean methodBelongsToUser = userMethods.stream()
                .anyMatch(method -> method.getPaymentMethodId().equals(methodId));

        if (!methodBelongsToUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        paymentMethodService.deletePaymentMethod(methodId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Set a payment method as the default
     *
     * @param methodId ID of the payment method to set as default
     * @return The updated payment method
     */
    @PatchMapping("/{methodId}/default")
    public ResponseEntity<PaymentMethod> setDefaultPaymentMethod(@PathVariable Integer methodId) {
        Integer userId = getCurrentUserId();

        // Verify the payment method belongs to the current user
        List<PaymentMethod> userMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        boolean methodBelongsToUser = userMethods.stream()
                .anyMatch(method -> method.getPaymentMethodId().equals(methodId));

        if (!methodBelongsToUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // First, reset all other payment methods to non-default
        userMethods.forEach(method -> {
            if (!method.getPaymentMethodId().equals(methodId) && method.getDefault()) {
                method.setDefault(false);
                paymentMethodService.updatePaymentMethod(method);
            }
        });

        // Set the specified method as default
        PaymentMethod defaultMethod = paymentMethodService.setDefaultPaymentMethod(methodId);
        return ResponseEntity.ok(defaultMethod);
    }

    /**
     * Helper method to get the current authenticated user's ID
     *
     * @return The user ID
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = authService.getUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user.getUserId();
    }
}