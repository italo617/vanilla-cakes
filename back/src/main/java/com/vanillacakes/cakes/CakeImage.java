package com.vanillacakes.cakes;

public record CakeImage(Long id, Long cakeId, String mimeType, byte[] content) {
}
