package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.Transaction;
import com.example.Bibliotech_backend.model.TransactionStatus;
import com.example.Bibliotech_backend.model.TransactionType;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.repository.TransactionRepository;
import com.example.Bibliotech_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final IdGeneratorService idGeneratorService;

    public TransactionService(IdGeneratorService idGeneratorService) {
        this.idGeneratorService = idGeneratorService;
    }

    public List<Transaction> getUserTransactions(Integer userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Optional<Transaction> getTransactionById(Integer transactionId) {
        return transactionRepository.findById(transactionId);
    }

    /**
     * Thêm mới giao dịch với ID tự tăng
     */
    @Transactional
    public Transaction addTransaction(Transaction transactionRequest) {
        logger.debug("Adding new transaction for user: {}", transactionRequest.getUserId());

        // Generate a new ID and set it on the request object directly
        if (transactionRequest.getTransactionId() == null) {
            int transactionID = idGeneratorService.generateTransactionId();
            transactionRequest.setTransactionId(transactionID);
        }

        // Set transaction date if not provided
        if (transactionRequest.getTransactionDate() == null) {
            transactionRequest.setTransactionDate(LocalDateTime.now());
        }

        if (transactionRequest.getStatus() == null) {
            transactionRequest.setStatus(TransactionStatus.PENDING);
        }

        // Save initial transaction
        Transaction savedTransaction = transactionRepository.save(transactionRequest);

        // Process based on transaction type and update user balance if applicable
        if (transactionRequest.getStatus() == TransactionStatus.COMPLETED) {
            updateUserBalance(transactionRequest);
        }

        return savedTransaction;
    }

    @Transactional
    public Transaction depositFunds(Transaction transaction) {
        // Generate ID if not provided
        if (transaction.getTransactionId() == null) {
            int transactionID = idGeneratorService.generateTransactionId();
            transaction.setTransactionId(transactionID);
        }

        // Validate transaction
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        // Set transaction type
        transaction.setTransactionType(TransactionType.DEPOSIT);

        // Process the deposit
        Users user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update user balance
        BigDecimal newBalance = user.getAccountBalance().add(transaction.getAmount());
        user.setAccountBalance(newBalance);
        userRepository.save(user);

        // Mark transaction as completed
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdrawFunds(Transaction transaction) {
        // Generate ID if not provided
        if (transaction.getTransactionId() == null) {
            int transactionID = idGeneratorService.generateTransactionId();
            transaction.setTransactionId(transactionID);
        }

        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        // Set transaction type
        transaction.setTransactionType(TransactionType.WITHDRAWAL);

        // Process the withdrawal
        Users user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user has sufficient balance
        if (user.getAccountBalance().compareTo(transaction.getAmount()) < 0) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setMetadata("Insufficient funds");
            return transactionRepository.save(transaction);
        }

        // Update user balance
        BigDecimal newBalance = user.getAccountBalance().subtract(transaction.getAmount());
        user.setAccountBalance(newBalance);
        userRepository.save(user);

        // Mark transaction as completed
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction processBookTransaction(Transaction transaction) {
        // This would handle MUA, THUE, REFUND transaction types
        if (transaction.getBookId() == null) {
            throw new IllegalArgumentException("Book ID is required for book transactions");
        }

        // Different logic based on transaction type
        switch (transaction.getTransactionType()) {
            case MUA:
            case THUE:
                // Check sufficient funds
                Users user = userRepository.findById(transaction.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                if (user.getAccountBalance().compareTo(transaction.getAmount()) < 0) {
                    transaction.setStatus(TransactionStatus.FAILED);
                    transaction.setMetadata("Insufficient funds");
                    return transactionRepository.save(transaction);
                }

                // Deduct balance
                BigDecimal newBalance = user.getAccountBalance().subtract(transaction.getAmount());
                user.setAccountBalance(newBalance);
                userRepository.save(user);
                break;

            case REFUND:
                // Add balance back
                Users refundUser = userRepository.findById(transaction.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                BigDecimal refundBalance = refundUser.getAccountBalance().add(transaction.getAmount());
                refundUser.setAccountBalance(refundBalance);
                userRepository.save(refundUser);
                break;

            default:
                throw new IllegalArgumentException("Invalid transaction type for book transaction");
        }

        // Mark transaction as completed
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction processSubscription(Transaction transaction) {
        // Handle subscription payments
        transaction.setTransactionType(TransactionType.SUBSCRIPTION);

        Users user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check sufficient funds
        if (user.getAccountBalance().compareTo(transaction.getAmount()) < 0) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setMetadata("Insufficient funds");
            return transactionRepository.save(transaction);
        }

        // Deduct balance
        BigDecimal newBalance = user.getAccountBalance().subtract(transaction.getAmount());
        user.setAccountBalance(newBalance);

        // Update premium status
        user.setIsPremium(true);
        userRepository.save(user);

        // Mark transaction as completed
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    private void updateUserBalance(Transaction transaction) {
        Users user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BigDecimal currentBalance = user.getAccountBalance();
        BigDecimal newBalance;

        // Update balance based on transaction type
        switch (transaction.getTransactionType()) {
            case DEPOSIT:
            case REFUND:
                newBalance = currentBalance.add(transaction.getAmount());
                break;

            case WITHDRAWAL:
            case MUA:
            case THUE:
            case SUBSCRIPTION:
                // Check if user has sufficient balance
                if (currentBalance.compareTo(transaction.getAmount()) < 0) {
                    throw new IllegalArgumentException("Insufficient funds");
                }
                newBalance = currentBalance.subtract(transaction.getAmount());
                break;

            default:
                throw new IllegalArgumentException("Unsupported transaction type");
        }

        user.setAccountBalance(newBalance);
        userRepository.save(user);
    }
}