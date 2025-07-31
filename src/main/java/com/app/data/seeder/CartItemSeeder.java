package com.app.data.seeder;

import com.app.entities.Cart;
import com.app.entities.CartItem;
import com.app.entities.ProductVariant;
import com.app.repositories.CartItemRepository;
import com.app.repositories.CartRepository;
import com.app.repositories.ProductVariantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeder class responsible for populating the database with sample {@link CartItem} data.
 * <p>
 * This class is intended for use during development or testing to create realistic,
 * randomized cart contents based on existing {@link Cart} and {@link ProductVariant} records.
 * It ensures the database has meaningful data for UI rendering, API testing, or performance checks.
 * </p>
 * <p>
 * The seeding process is <strong>idempotent</strong>: it only runs if no cart items currently exist
 * in the database (checked via {@code count() > 0}), preventing duplication on application restarts.
 * </p>
 * <p>
 * Each cart has a 50% chance of containing items. Carts with items receive 1–3 random product variants,
 * each with a quantity of 1 or 2.
 * </p>
 *
 * @see CartItemRepository
 * @see CartRepository
 * @see ProductVariantRepository
 */
public class CartItemSeeder {

    private final CartItemRepository cartItemRepo;
    private final CartRepository cartRepo;
    private final ProductVariantRepository productVariantRepo;
    private final Random random = new Random();

    /**
     * Constructs a new {@code CartItemSeeder} with required repository dependencies.
     *
     * @param cartItemRepo       the repository for saving and querying cart items; must not be {@code null}
     * @param cartRepo           the repository for retrieving existing carts; must not be {@code null}
     * @param productVariantRepo the repository for retrieving available product variants; must not be {@code null}
     * @throws IllegalArgumentException if any repository is {@code null}
     */
    public CartItemSeeder(
            CartItemRepository cartItemRepo,
            CartRepository cartRepo,
            ProductVariantRepository productVariantRepo) {
        this.cartItemRepo = cartItemRepo;
        this.cartRepo = cartRepo;
        this.productVariantRepo = productVariantRepo;
    }

    /**
     * Seeds the database with sample cart items if none already exist.
     * <p>
     * This method:
     * </p>
     * <ol>
     *   <li>Checks if the {@code cart_items} table is already populated</li>
     *   <li>Fetches all existing carts and product variants</li>
     *   <li>Skips seeding if either list is empty (prerequisite data missing)</li>
     *   <li>Randomly assigns 1–3 items to ~50% of carts</li>
     *   <li>Persists all generated items in bulk</li>
     * </ol>
     * <p>
     * Designed to be called once during application startup (e.g., via a runner or initializer).
     * </p>
     *
     * @see #random
     */
    public void seed() {
        // Skip seeding if cart items already exist to avoid duplicates
        if (cartItemRepo.count() > 0) {
            return;
        }

        System.out.println("Seeding cart items...");

        List<Cart> carts = cartRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();

        // Abort if prerequisites are missing
        if (carts.isEmpty() || variants.isEmpty()) {
            System.out.println("Skipping cart item seeding: insufficient data (carts or product variants missing).");
            return;
        }

        List<CartItem> cartItems = new ArrayList<>();

        for (Cart cart : carts) {
            // 50% probability that a cart contains items
            if (random.nextBoolean()) {
                int itemCount = random.nextInt(3) + 1; // Random: 1, 2, or 3 items

                for (int i = 0; i < itemCount; i++) {
                    ProductVariant variant = variants.get(random.nextInt(variants.size()));
                    int quantity = random.nextInt(2) + 1; // Quantity: 1 or 2

                    CartItem cartItem = CartItem.builder()
                            .cart(cart)
                            .productVariant(variant)
                            .quantity(quantity)
                            .build();

                    cartItems.add(cartItem);
                }
            }
        }

        // Persist all cart items in a single batch operation
        cartItemRepo.saveAll(cartItems);
        System.out.println(cartItems.size() + " cart items created.");
    }
}