package com.app.services;

import com.app.entities.Product;
import com.app.entities.Wishlist;
import com.app.repositories.ProductRepository;
import com.app.repositories.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing business logic related to user wishlists.
 * <p>
 * A wishlist allows users to save products for later viewing or purchase.
 * This service acts as the intermediary between the controller and repository layers,
 * and is designed to support operations such as:
 * </p>
 * <ul>
 *   <li>Creating and managing user-specific wishlists</li>
 *   <li>Adding or removing products from a wishlist</li>
 *   <li>Retrieving wishlist items by user or product</li>
 *   <li>Preventing duplicates in a user's wishlist</li>
 *   <li>Supporting wishlist sharing or export features</li>
 *   <li>Integrating with recommendations ("items you saved")</li>
 * </ul>
 * <p>
 * Currently, this class holds a dependency on {@link WishlistRepository}
 * and is structured to support future implementation of these features.
 * All data persistence and retrieval operations are delegated to the repository.
 * </p>
 */
@Service
public class WishlistService {

    // Repository for performing CRUD operations on wishlist entities
    private final WishlistRepository wishlistRepo;
    private final ProductRepository productRepo;

    /**
     * Constructs a new WishlistService with the required repository dependencies.
     *
     * @param wishlistRepo the repository used to interact with wishlist data; must not be null
     * @param productRepo  the repository used to interact with product data; must not be null
     */
    @Autowired
    public WishlistService(WishlistRepository wishlistRepo, ProductRepository productRepo) {
        this.wishlistRepo = wishlistRepo;
        this.productRepo = productRepo;
    }

    public Wishlist addProductToWishlist(Long wishlistId, Long productId) {
        Wishlist wishlist = wishlistRepo.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        wishlist.getProducts().add(product);
        return wishlistRepo.save(wishlist);
    }

    public Wishlist removeProductFromWishlist(Long wishlistId, Long productId) {
        Wishlist wishlist = wishlistRepo.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        wishlist.getProducts().remove(product);
        return wishlistRepo.save(wishlist);
    }
}