package com.app.data.seeder;

import com.app.entities.Product;
import com.app.entities.User;
import com.app.entities.Wishlist;
import com.app.repositories.ProductRepository;
import com.app.repositories.UserRepository;
import com.app.repositories.WishlistRepository;

import java.util.*;

/**
 * Seeder class responsible for populating the database with realistic user wishlist data.
 * <p>
 * This class:
 * <ul>
 *   <li>Creates 1–2 wishlists per user</li>
 *   <li>Populates each wishlist with 1–5 randomly selected products</li>
 *   <li>Generates descriptive titles and default placeholder metadata</li>
 *   <li>Ensures data consistency by checking for existing records before seeding</li>
 * </ul>
 * <p>
 * Designed to run during application startup. Skips seeding if wishlists already exist.
 * </p>
 */
public class WishlistSeeder {

    private final WishlistRepository wishlistRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final Random random = new Random();

    /**
     * Constructs a new WishlistSeeder with required dependencies.
     *
     * @param wishlistRepo the repository for persisting wishlists
     * @param userRepo     the repository to fetch users who will own the wishlists
     * @param productRepo  the repository to fetch products to include in wishlists
     */
    public WishlistSeeder(WishlistRepository wishlistRepo,
                          UserRepository userRepo,
                          ProductRepository productRepo) {
        this.wishlistRepo = wishlistRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    /**
     * Seeds the database with user wishlists if none already exist.
     * <p>
     * For each user:
     * <ul>
     *   <li>Creates 1–2 wishlists</li>
     *   <li>Assigns a generated title and description</li>
     *   <li>Sets placeholder image and product URLs</li>
     *   <li>Adds a random subset of 1–5 products from the existing product pool</li>
     *   <li>Uses "Varies" for price and "Mixed" for category as defaults</li>
     * </ul>
     * </p>
     * <p>
     * Skips execution if wishlists already exist or if there are no users/products.
     * </p>
     */
    public void seed() {
        // Skip seeding if wishlists already exist
        if (wishlistRepo.count() > 0) {
            return;
        }

        System.out.println("Seeding wishlists...");

        List<User> users = userRepo.findAll();
        List<Product> products = productRepo.findAll();

        // Ensure there are users and products to work with
        if (users.isEmpty() || products.isEmpty()) {
            System.out.println("Skipping wishlist seeding: Not enough data (users or products missing).");
            return;
        }

        List<Wishlist> wishlists = new ArrayList<>();

        // Generate 1–2 wishlists per user
        for (User user : users) {
            int numWishlists = random.nextInt(2) + 1; // 1 or 2

            for (int i = 0; i < numWishlists; i++) {
                Set<Product> selectedProducts = new HashSet<>(
                        getRandomSubset(products, random.nextInt(5) + 1) // 1 to 5 products
                );

                Wishlist wishlist = Wishlist.builder()
                        .title("Wishlist " + (i + 1) + " of " + user.getUsername())
                        .description("Auto-generated wishlist for " + user.getUsername())
                        .imageUrl("https://via.placeholder.com/150") // Placeholder image
                        .productUrl("https://example.com/product") // Example domain
                        .price("Varies") // Dynamic based on products
                        .category("Mixed") // Aggregated category
                        .user(user)
                        .products(selectedProducts)
                        .build();

                wishlists.add(wishlist);
            }
        }

        // Persist all generated wishlists
        wishlistRepo.saveAll(wishlists);
        System.out.println(wishlists.size() + " wishlists created.");
    }

    /**
     * Returns a random subset of a given list with the specified size.
     * The original list is not modified.
     *
     * @param list the source list to sample from
     * @param size the desired number of elements in the subset
     * @param <T>  the type of elements in the list
     * @return a new list containing a random subset of elements
     */
    private <T> List<T> getRandomSubset(List<T> list, int size) {
        size = Math.min(size, list.size()); // Prevent IndexOutOfBoundsException
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy, random);
        return copy.subList(0, size);
    }
}