package com.vanillacakes.cakes;

import java.math.BigDecimal;

public record CreateCakeRequest(
        String name,
        String description,
        BigDecimal price,
        boolean active) {
}
