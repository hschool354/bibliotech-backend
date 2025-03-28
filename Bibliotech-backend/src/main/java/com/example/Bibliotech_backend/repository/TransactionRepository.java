package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.Transaction;
import com.example.Bibliotech_backend.model.TransactionStatus;
import com.example.Bibliotech_backend.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserId(Integer userId);
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Integer userId);

    List<Transaction> findByUserIdAndTransactionTypeAndStatus(
            Integer userId, TransactionType transactionType, TransactionStatus status);
}