package com.vanillacakes.cakes;

public record CreateCakeImageRequest(Long cakeId, String mimeType, String contentBase64) {
}
