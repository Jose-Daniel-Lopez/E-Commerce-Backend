package com.app.controllers;

import com.app.DTO.ShippingAddressDTO;
import com.app.entities.ShippingAddress;
import com.app.services.ShippingAddressService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing shipping addresses.
 * <p>
 * Provides full CRUD operations for shipping addresses, including:
 * <ul>
 *   <li>Creating, reading, updating, and deleting addresses</li>
 *   <li>Retrieving addresses by user or order</li>
 *   <li>Assigning an address to an order</li>
 *   <li>Getting a user's default address</li>
 * </ul>
 * </p>
 * <p>
 * All requests use {@link ShippingAddressDTO} for input/output to decouple
 * internal entities from the API contract.
 * </p>
 * <p>
 * Base URL: {@code /api/shippingAddresses}
 * </p>
 */
@RestController
@RequestMapping("/api/shippingAddresses")
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService;

    @Autowired
    public ShippingAddressController(ShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    // === CREATE ===

    /**
     * Creates a new shipping address.
     *
     * @param dto the shipping address data (validated)
     * @return 201 Created with the created address DTO, or 400/500 on error
     * @response 201 Successfully created
     * @response 400 Invalid data or missing required fields
     * @response 500 Internal server error
     */
    @PostMapping
    public ResponseEntity<?> createShippingAddress(@Valid @RequestBody ShippingAddressDTO dto) {
        try {
            ShippingAddress created = shippingAddressService.createShippingAddress(dto);
            ShippingAddressDTO response = convertToDTO(created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Duplicate or invalid address data"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating address: " + e.getMessage()));
        }
    }

    // === READ ===

    /**
     * Retrieves all shipping addresses in the system.
     * <p>
     * Primarily for admin use or debugging.
     * </p>
     *
     * @return 200 OK with list of DTOs, or 500 on error
     * @response 200 Returns all addresses (may be empty)
     * @response 500 Internal server error
     */
    @GetMapping
    public ResponseEntity<List<ShippingAddressDTO>> getAllShippingAddresses() {
        try {
            List<ShippingAddress> addresses = shippingAddressService.findAll();
            List<ShippingAddressDTO> dtos = addresses.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves all shipping addresses associated with a specific user.
     *
     * @param userId the ID of the user
     * @return 200 OK with list of DTOs, or 500 on error
     * @response 200 Returns user's addresses (may be empty)
     * @response 500 Internal server error
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAddressesByUser(@PathVariable Long userId) {
        try {
            List<ShippingAddress> addresses = shippingAddressService.findByUserId(userId);
            List<ShippingAddressDTO> dtos = addresses.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving user addresses"));
        }
    }

    /**
     * Retrieves a specific shipping address by ID.
     *
     * @param id the unique identifier of the address
     * @return 200 OK if found, 404 if not found, or 500 on error
     * @response 200 Address found
     * @response 404 Address not found
     * @response 500 Internal server error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getShippingAddressById(@PathVariable Long id) {
        try {
            Optional<ShippingAddress> address = shippingAddressService.findById(id);

            return address.map(a -> ResponseEntity.ok(convertToDTO(a)))
                    .orElseGet(ResponseEntity.notFound()::build);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving address"));
        }
    }

    /**
     * Retrieves the shipping address assigned to a specific order.
     *
     * @param orderId the ID of the order
     * @return 200 OK if address is assigned, 404 if not, or 500 on error
     * @response 200 Address found and assigned
     * @response 404 No address assigned or order not found
     * @response 500 Internal server error
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderShippingAddress(@PathVariable Long orderId) {
        try {
            Optional<ShippingAddress> address = shippingAddressService.getOrderShippingAddress(orderId);

            return address.map(a -> ResponseEntity.ok(convertToDTO(a)))
                    .orElseGet(ResponseEntity.notFound()::build);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving order address"));
        }
    }

    /**
     * Retrieves the default shipping address for a user.
     * <p>
     * The "default" address is typically the first, most recently used,
     * or user-designated primary address (logic defined in service).
     * </p>
     *
     * @param userId the ID of the user
     * @return 200 OK if default exists, 404 if none, or 500 on error
     * @response 200 Default address found
     * @response 404 No addresses exist for the user
     * @response 500 Internal server error
     */
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<?> getUserDefaultAddress(@PathVariable Long userId) {
        try {
            Optional<ShippingAddress> address = shippingAddressService.getUserDefaultAddress(userId);

            if (address.isPresent()) {
                return ResponseEntity.ok(convertToDTO(address.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No addresses found for this user"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving default address"));
        }
    }

    // === UPDATE ===

    /**
     * Updates an existing shipping address.
     *
     * @param id  the ID of the address to update
     * @param dto the updated address data
     * @return 200 OK with updated DTO, 404 if not found, or 500 on error
     * @response 200 Successfully updated
     * @response 404 Address not found
     * @response 500 Internal server error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShippingAddress(
            @PathVariable Long id,
            @Valid @RequestBody ShippingAddressDTO dto) {

        try {
            dto.setId(id);
            ShippingAddress updated = shippingAddressService.updateShippingAddress(dto);
            return ResponseEntity.ok(convertToDTO(updated));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Address not found with ID: " + id));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Invalid or duplicate address data"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating address: " + e.getMessage()));
        }
    }

    // === DELETE ===

    /**
     * Deletes a shipping address by ID.
     * <p>
     * Deletion may be denied if the address is currently assigned to an order.
     * </p>
     *
     * @param id the ID of the address to delete
     * @return 200 OK with success message, 404 if not found, 409 if in use, or 500 on error
     * @response 200 Successfully deleted
     * @response 404 Address not found
     * @response 409 Address is in use and cannot be deleted
     * @response 500 Internal server error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShippingAddress(@PathVariable Long id) {
        try {
            shippingAddressService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Address deleted successfully"));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Address not found with ID: " + id));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting address"));
        }
    }

    // === ASSIGNMENT ===

    /**
     * Assigns a shipping address to an order.
     *
     * @param request JSON body containing "orderId" and "addressId"
     * @return 200 OK on success, 400 if IDs missing, 404 if not found, or 500 on error
     * @response 200 Successfully assigned
     * @response 400 Missing orderId or addressId
     * @response 404 Order or address not found
     * @response 500 Internal server error
     */
    @PostMapping("/assign-to-order")
    public ResponseEntity<?> assignAddressToOrder(@RequestBody Map<String, Long> request) {
        try {
            Long orderId = request.get("orderId");
            Long addressId = request.get("addressId");

            if (orderId == null || addressId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "orderId and addressId are required"));
            }

            shippingAddressService.assignAddressToOrder(orderId, addressId);
            return ResponseEntity.ok(Map.of("message", "Address assigned successfully"));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error assigning address"));
        }
    }

    // === HELPER METHODS ===

    /**
     * Converts a {@link ShippingAddress} entity to its DTO representation.
     *
     * @param entity the entity to convert
     * @return a populated {@link ShippingAddressDTO}
     */
    private ShippingAddressDTO convertToDTO(ShippingAddress entity) {
        ShippingAddressDTO dto = new ShippingAddressDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setAddressType(entity.getAddressType().name());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setZipCode(entity.getZipCode());
        dto.setCountry(entity.getCountry());
        dto.setUserId(entity.getUser().getId());
        return dto;
    }
}