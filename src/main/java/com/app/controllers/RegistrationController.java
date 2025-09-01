package com.app.controllers;

import com.app.DTO.RegisterDTO;
import com.app.entities.Cart;
import com.app.entities.User;
import com.app.entities.Wishlist;
import com.app.repositories.CartRepository;
import com.app.repositories.UserRepository;
import com.app.hateoas.HateoasLinkBuilder;
import com.app.hateoas.UserRepresentation;
import com.app.repositories.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for handling user registration and email verification.
 * <p>
 * Provides endpoints for:
 * <ul>
 *   <li>User registration with unique username/email checks</li>
 *   <li>Email verification via token</li>
 * </ul>
 * </p>
 * <p>
 * Endpoint: {@code /api/auth}
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Allow frontend dev server
public class RegistrationController {

    private final UserRepository userRepo;
    private final CartRepository cartRepo;
    private final WishlistRepository wishlistRepo;
    private final PasswordEncoder passwordEncoder;
    private final HateoasLinkBuilder linkBuilder;

    /**
     * Constructs a new RegistrationController with required dependencies.
     *
     * @param userRepo         repository for user persistence and queries
     * @param cartRepo         repository for cart persistence
     * @param wishlistRepo     repository for wishlist persistence
     * @param passwordEncoder  component to securely hash passwords
     * @param linkBuilder       HATEOAS link builder for creating hypermedia links
     */
    @Autowired
    public RegistrationController(UserRepository userRepo, CartRepository cartRepo, WishlistRepository wishlistRepo,
                                PasswordEncoder passwordEncoder, HateoasLinkBuilder linkBuilder) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.wishlistRepo = wishlistRepo;
        this.passwordEncoder = passwordEncoder;
        this.linkBuilder = linkBuilder;
    }

    /**
     * Registers a new user after validating username and email uniqueness.
     * <p>
     * On success, creates a new unverified user with a verification token and shopping cart.
     * Returns basic user info and the token (for dev/testing; in production,
     * this should be sent via email only).
     * </p>
     *
     * @param registerDTO the registration data (username, email, password, etc.)
     * @return {@link ResponseEntity} with success or error details
     * @response 200 User created successfully (unverified)
     * @response 409 Username or email already exists
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processRegistration(@RequestBody RegisterDTO registerDTO) {
        // Validate uniqueness of username
        if (userRepo.existsByUsername(registerDTO.getUsername())) {
            return errorResponse("Username already exists", HttpStatus.CONFLICT);
        }

        // Validate uniqueness of email
        if (userRepo.existsByEmail(registerDTO.getEmail())) {
            return errorResponse("Email already registered", HttpStatus.CONFLICT);
        }

        // Set default avatar if none provided
        String avatar = (registerDTO.getAvatar() != null && !registerDTO.getAvatar().trim().isEmpty())
                ? registerDTO.getAvatar()
                : "user.png";

        // Build and configure new user
        User newUser = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .avatar(avatar)
                .role(registerDTO.getRole() != null ? registerDTO.getRole() : User.Role.CUSTOMER)
                .build();

        // Generate email verification token
        String verificationToken = UUID.randomUUID().toString();
        newUser.setVerificationToken(verificationToken);
        newUser.setVerified(false);

        // Persist user to database
        User savedUser = userRepo.save(newUser);

        // Create a shopping cart for the new user
        Cart cart = Cart.builder()
                .user(savedUser)
                .createdAt(LocalDateTime.now())
                .build();
        Cart savedCart = cartRepo.save(cart);

        // Create a default wishlist for the new user
        Wishlist defaultWishlist = Wishlist.builder()
                .title("My Wishlist")
                .description("Your personal wishlist")
                .imageUrl("wishlist.png")
                .productUrl("")
                .price("Varies")
                .category("Mixed")
                .user(savedUser)
                .build();
        wishlistRepo.save(defaultWishlist);

        // Set bidirectional relationship
        savedUser.setCart(savedCart);
        savedUser = userRepo.save(savedUser);

        // Return success response with minimal user info
        return ResponseEntity.ok(createSuccessResponse(savedUser));
    }

    /**
     * Verifies a user's email using a token sent during registration.
     * <p>
     * If the token is valid, marks the user as verified and removes the token.
     * </p>
     *
     * @param token the verification token sent in the email
     * @return {@link ResponseEntity} with success or error message
     * @response 200 Account verified successfully
     * @response 400 Invalid or expired token
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        User user = userRepo.findByVerificationToken(token);

        if (user == null) {
            return errorResponse("Invalid or expired verification token", HttpStatus.BAD_REQUEST);
        }

        // Mark user as verified and clear the token
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepo.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "Account verified successfully"));
    }

    // === Private Helper Methods ===

    /**
     * Creates a standardized error response.
     *
     * @param message the error message to include
     * @param status  the HTTP status to return
     * @return a {@link ResponseEntity} containing an error map
     */
    private ResponseEntity<Map<String, String>> errorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(Collections.singletonMap("error", message));
    }

    /**
     * Creates a standardized success response with user details and HATEOAS links.
     * <p>
     * Note: The {@code verificationToken} is included for testing convenience.
     * In a production system, this should NEVER be returned to the client.
     * It should only be sent via secure email.
     * </p>
     *
     * @param user the saved user entity
     * @return a map containing user info, success message, and HATEOAS links
     */
    private Map<String, Object> createSuccessResponse(User user) {
        // Create HATEOAS-enabled user representation with all proper links
        UserRepresentation userRepresentation = linkBuilder.buildUserRepresentation(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", userRepresentation);
        response.put("verificationToken", user.getVerificationToken()); // ⚠️ For dev only

        return response;
    }
}