package com.example.Bibliotech_backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "PaymentMethods")
public class PaymentMethod {

    @Id
    @Column(name = "payment_method_id")
    private Integer paymentMethodId;

    @Column(name = "user_id",nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_type",nullable = false)
    private MethodType methodType;

    @Column(name = "card_number",length = 16)
    private String cardNumber;

    @Column(name = "card_holder",length = 255)
    private String cardHolder;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "is_default",nullable = false)
    private Boolean isDefault = false;

    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

    // Getters and setters
    public PaymentMethod() {
    }

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public enum MethodType {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
    }
}