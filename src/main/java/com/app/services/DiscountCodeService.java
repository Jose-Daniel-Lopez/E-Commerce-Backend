package com.app.services;

import com.app.repositories.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing business logic related to discount codes.
 * <p>
 * This service serves as a central layer between the controller and repository,
 * designed to encapsulate rules such as:
 * </p>
 * <ul>
 *   <li>Validating code applicability (expiry, usage limits)</li>
 *   <li>Checking eligibility (user, cart total, product scope)</li>
 *   <li>Tracking redemptions</li>
 *   <li>Handling case-insensitive lookups</li>
 *   <li>Applying discount calculations (if logic resides here)</li>
 * </ul>
 * <p>
 * Currently, this class holds a dependency on {@link DiscountCodeRepository}
 * and is prepared for future implementation of these features.
 * All data access operations are delegated to the injected repository.
 * </p>
 */
@Service
public class DiscountCodeService {

    // Repository for data persistence and retrieval of discount codes
    private final DiscountCodeRepository discountCodeRepo;

    /**
     * Constructs a new DiscountCodeService with the required repository.
     *
     * @param discountCodeRepo the repository used to interact with discount code data;
     *                         must not be null
     */
    @Autowired
    public DiscountCodeService(DiscountCodeRepository discountCodeRepo) {
        this.discountCodeRepo = discountCodeRepo;
    }

    // Future methods (suggested additions):
    //
    // public Optional<DiscountCode> findByCode(String code) { ... }
    // public boolean isValidAndApplicable(String code, Long userId, BigDecimal cartTotal) { ... }
    // public DiscountCode createDiscountCode(DiscountCode code) { ... }
    // public void incrementUsage(String code) { ... }
    // public boolean isCodeExpired(String code) { ... }
}