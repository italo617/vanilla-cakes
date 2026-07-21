package com.vanillacakes.cakes;

import java.io.InputStream;

public record CakeImageContent(String mimeType, InputStream stream) {
}
