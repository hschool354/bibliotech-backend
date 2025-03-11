package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.PaymentMethod;
import com.example.Bibliotech_backend.model.Transaction;
import com.example.Bibliotech_backend.model.TransactionStatus;
import com.example.Bibliotech_backend.repository.PaymentMethodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private IdGeneratorService idGeneratorService;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodService.class);

    public List<PaymentMethod> getPaymentMethodsByUserId(Integer userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    @Transactional
    public PaymentMethod addPaymentMethod(PaymentMethod paymentMethodRequest) {
        logger.debug("Adding new payment method for user: {}", paymentMethodRequest.getUserId());

        // Generate a new ID and set it on the request object directly
        if (paymentMethodRequest.getPaymentMethodId() == null) {
            int paymentMethodId = idGeneratorService.generatePaymentMethodId();
            paymentMethodRequest.setPaymentMethodId(paymentMethodId);
        }

        // Set created date if not provided
        if (paymentMethodRequest.getCreatedAt() == null) {
            paymentMethodRequest.setCreatedAt(LocalDate.now());
        }

        // If this is the first payment method for the user, set it as default
        List<PaymentMethod> existingMethods = paymentMethodRepository.findByUserId(paymentMethodRequest.getUserId());
        if (existingMethods.isEmpty()) {
            paymentMethodRequest.setDefault(true);
        }

        // Save payment method
        return paymentMethodRepository.save(paymentMethodRequest);
    }

    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    public void deletePaymentMethod(Integer methodId) {
        paymentMethodRepository.deleteById(methodId);
    }

    public PaymentMethod setDefaultPaymentMethod(Integer methodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId).orElseThrow(() -> new RuntimeException("Payment method not found"));
        paymentMethod.setDefault(true);
        return paymentMethodRepository.save(paymentMethod);
    }
}