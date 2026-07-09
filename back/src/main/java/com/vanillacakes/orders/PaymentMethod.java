package com.vanillacakes.orders;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CASH_ON_DELIVERY("cash_on_delivery"),
    CREDIT_CARD("credit_card"),
    DEBIT_CARD("debit_card");

    private final String identifier;

    PaymentMethod(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    // Used by Jackson during JSON serialization.
    @JsonValue
    public String getJsonValue() {
        return identifier;
    }

    public static PaymentMethod fromIdentifier(String identifier) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.getIdentifier().equals(identifier)) {
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("Invalid payment method: " + identifier);
    }
}
