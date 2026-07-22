package com.eventsystem.models;

public class PaymentDetails {
    private String cardName;
    private String cardNumber;
    private String expiryDate;
    private String cvc;

    public PaymentDetails(String cardName, String cardNumber, String expiryDate, String cvc) {
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvc = cvc;
    }

    public String getCardName() { return cardName; }
    public String getCardNumber() { return cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvc() { return cvc; }

  
    public boolean isValid() {
        return true; 
    }
}