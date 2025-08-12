package com.app.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service for fetching product images from Pexels API.
 * Provides category-specific image searches with fallback mechanisms.
 * More generous rate limits compared to Unsplash (200 requests per hour).
 */
@Service
public class PexelsService {

    @Value("${pexels.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    // Cache to avoid duplicate API calls for the same search terms
    private final Map<String, List<String>> imageCache = new HashMap<>();

    public PexelsService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gets a random image URL for a product based on its category and brand.
     */
    public String getProductImage(String categoryName, String brand, String productName) {
        String searchTerm = buildSearchTerm(categoryName, brand, productName);
        return getRandomImageUrl(searchTerm);
    }

    /**
     * Gets a random image URL for a product variant based on color.
     */
    public String getVariantImage(String categoryName, String brand, String color) {
        String searchTerm = buildVariantSearchTerm(categoryName, brand, color);
        return getRandomImageUrl(searchTerm);
    }

    private String buildSearchTerm(String categoryName, String brand, String productName) {
        // Create search terms based on category
        return switch (categoryName.toLowerCase()) {
            case "smartphones" -> brand.toLowerCase() + " smartphone mobile phone";
            case "tablets" -> brand.toLowerCase() + " tablet ipad";
            case "computers", "laptops" -> brand.toLowerCase() + " laptop computer notebook";
            case "keyboards" -> brand.toLowerCase() + " keyboard mechanical gaming";
            case "mice" -> brand.toLowerCase() + " mouse gaming computer";
            case "controllers" -> "gaming controller gamepad " + brand.toLowerCase();
            case "handhelds" -> "handheld gaming console portable";
            default -> categoryName.toLowerCase() + " " + brand.toLowerCase();
        };
    }

    private String buildVariantSearchTerm(String categoryName, String brand, String color) {
        String baseSearch = buildSearchTerm(categoryName, brand, "");
        return baseSearch + " " + color.toLowerCase();
    }

    private String getRandomImageUrl(String searchTerm) {
        try {
            // Check cache first
            if (imageCache.containsKey(searchTerm)) {
                List<String> cachedUrls = imageCache.get(searchTerm);
                if (!cachedUrls.isEmpty()) {
                    return cachedUrls.get(random.nextInt(cachedUrls.size()));
                }
            }

            // Build API URL for Pexels
            String apiUrl = UriComponentsBuilder
                    .fromHttpUrl("https://api.pexels.com/v1/search")
                    .queryParam("query", searchTerm)
                    .queryParam("per_page", "15")
                    .queryParam("orientation", "landscape")
                    .build()
                    .toUriString();

            // Set up headers with API key
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make API call
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode photos = jsonNode.get("photos");

            List<String> imageUrls = new ArrayList<>();
            if (photos != null && photos.isArray()) {
                for (JsonNode photo : photos) {
                    JsonNode src = photo.get("src");
                    if (src != null && src.has("medium")) {
                        imageUrls.add(src.get("medium").asText());
                    }
                }
            }

            // Cache the results
            imageCache.put(searchTerm, imageUrls);

            // Return random image URL or fallback
            if (!imageUrls.isEmpty()) {
                return imageUrls.get(random.nextInt(imageUrls.size()));
            }

        } catch (Exception e) {
            System.err.println("Error fetching image from Pexels: " + e.getMessage());
        }

        // Fallback to placeholder
        return getFallbackImage();
    }

    private String getFallbackImage() {
        // Use a placeholder service as fallback
        int width = 640;
        int height = 480;
        int seed = ThreadLocalRandom.current().nextInt(1, 1000);
        return String.format("https://picsum.photos/seed/%d/%d/%d", seed, width, height);
    }

    /**
     * Clears the image cache. Useful for testing or memory management.
     */
    public void clearCache() {
        imageCache.clear();
    }
}
