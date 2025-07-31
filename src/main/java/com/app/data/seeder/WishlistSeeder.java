package com.app.data.seeder;

import com.app.entities.Product;
import com.app.entities.User;
import com.app.entities.Wishlist;
import com.app.repositories.ProductRepository;
import com.app.repositories.UserRepository;
import com.app.repositories.WishlistRepository;

import java.util.*;

public class WishlistSeeder {

    private final WishlistRepository wishlistRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final Random random = new Random();

    public WishlistSeeder(WishlistRepository wishlistRepo, UserRepository userRepo, ProductRepository productRepo) {
        this.wishlistRepo = wishlistRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public void seed() {
        if (wishlistRepo.count() > 0) return;
        System.out.println("Seeding wishlists...");
        List<User> users = userRepo.findAll();
        List<Product> products = productRepo.findAll();
        if (users.isEmpty() || products.isEmpty()) return;
        List<Wishlist> wishlists = new ArrayList<>();
        for (User user : users) {
            int numWishlists = random.nextInt(2) + 1;
            for (int i = 0; i < numWishlists; i++) {
                Wishlist wishlist = Wishlist.builder()
                        .title("Wishlist " + (i + 1) + " of " + user.getUsername())
                        .description("Auto-generated wishlist for " + user.getUsername())
                        .imageUrl("https://via.placeholder.com/150")
                        .productUrl("https://example.com/product")
                        .price("Varies")
                        .category("Mixed")
                        .user(user)
                        .products(new HashSet<>(getRandomSubset(products, random.nextInt(5) + 1)))
                        .build();
                wishlists.add(wishlist);
            }
        }
        wishlistRepo.saveAll(wishlists);
        System.out.println(wishlists.size() + " wishlists created.");
    }

    private <T> List<T> getRandomSubset(List<T> list, int size) {
        size = Math.min(size, list.size());
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy, random);
        return copy.subList(0, size);
    }
}

