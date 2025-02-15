package com.example.Bibliotech_backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;

@Service
public class IdGeneratorService {
    private final ReentrantLock lock = new ReentrantLock();

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public int generateUserId() {
        lock.lock();
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(user_id), 0) FROM Users"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public int generateBookId() {
        lock.lock();
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(book_id), 0) FROM Books"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public int generateCategoryId() {
        lock.lock();
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(category_id), 0) FROM Categories"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock();
        }
    }
}