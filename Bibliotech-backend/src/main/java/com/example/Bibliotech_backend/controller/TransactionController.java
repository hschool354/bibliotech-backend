package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.model.Transaction;
import com.example.Bibliotech_backend.service.TransactionService;
import com.example.Bibliotech_backend.service.AuthService;
import com.example.Bibliotech_backend.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public List<Transaction> getUserTransactions(@RequestParam(required = false) Integer userId) {
        if (userId == null) {
            // Get the authenticated user's username from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Get user info from username
            Users user = authService.getUserByUsername(username);
            if (user != null) {
                userId = user.getUserId();
            } else {
                // Admin or special case - might need specific handling
                // For now, return empty list or throw appropriate exception
                return List.of(); // Empty list
            }
        }

        return transactionService.getUserTransactions(userId);
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        try {
            // Only use the transaction's userId if it's provided, otherwise get from auth
            if (transaction.getUserId() == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();

                Users user = authService.getUserByUsername(username);
                if (user != null) {
                    transaction.setUserId(user.getUserId());
                }
            }

            // Handle empty transaction date
            if (transaction.getTransactionDate() == null) {
                transaction.setTransactionDate(LocalDateTime.now());
            }

            Transaction result = transactionService.addTransaction(transaction);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Transaction failed: " + e.getMessage());
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer transactionId) {
        return transactionService.getTransactionById(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositFunds(@RequestBody Transaction transaction) {
        try {
            // Only use the transaction's userId if it's provided, otherwise get from auth
            if (transaction.getUserId() == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();

                Users user = authService.getUserByUsername(username);
                if (user != null) {
                    transaction.setUserId(user.getUserId());
                }
            }

            Transaction result = transactionService.depositFunds(transaction);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Deposit failed: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawFunds(@RequestBody Transaction transaction) {
        try {
            // Only use the transaction's userId if it's provided, otherwise get from auth
            if (transaction.getUserId() == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();

                Users user = authService.getUserByUsername(username);
                if (user != null) {
                    transaction.setUserId(user.getUserId());
                }
            }

            Transaction result = transactionService.withdrawFunds(transaction);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Withdrawal failed: " + e.getMessage());
        }
    }
}