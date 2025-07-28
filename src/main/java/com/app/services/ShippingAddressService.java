package com.app.services;

import com.app.DTO.ShippingAddressDTO;
import com.app.entities.Order;
import com.app.entities.ShippingAddress;
import com.app.entities.User;
import com.app.repositories.OrderRepository;
import com.app.repositories.ShippingAddressRepository;
import com.app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing {@link ShippingAddress} entities and their relationships.
 * <p>
 * This service handles all business logic related to user shipping addresses, including:
 * </p>
 * <ul>
 *   <li>Creating, updating, and deleting addresses</li>
 *   <li>Validating ownership and usage constraints</li>
 *   <li>Assigning addresses to orders with cross-entity validation</li>
 *   <li>Supporting queries by user, type, or default selection</li>
 *   <li>Preventing deletion of in-use addresses</li>
 * </ul>
 * <p>
 * Ensures data integrity by enforcing that:
 * </p>
 * <ul>
 *   <li>Only existing users can own addresses</li>
 *   <li>An address must belong to the same user as the order it's assigned to</li>
 *   <li>Addresses in active orders cannot be deleted</li>
 * </ul>
 * <p>
 * Uses declarative transactions: write operations are transactional, read-only methods are optimized.
 * </p>
 */
@Service
@Transactional
public class ShippingAddressService {

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // === Address Creation & Management ===

    /**
     * Creates a new shipping address for a user.
     *
     * @param dto the data transfer object containing address details and user ID
     * @return the saved {@link ShippingAddress}
     * @throws EntityNotFoundException if the specified user does not exist
     */
    public ShippingAddress createShippingAddress(ShippingAddressDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + dto.getUserId()
                ));

        ShippingAddress shippingAddress = ShippingAddress.builder()
                .title(dto.getTitle())
                .addressType(ShippingAddress.AddressType.valueOf(dto.getAddressType()))
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .country(dto.getCountry())
                .user(user)
                .build();

        return shippingAddressRepository.save(shippingAddress);
    }

    /**
     * Updates an existing shipping address with new details.
     *
     * @param dto the DTO containing updated address data and ID
     * @return the updated {@link ShippingAddress}
     * @throws EntityNotFoundException if the address or new user is not found
     */
    public ShippingAddress updateShippingAddress(ShippingAddressDTO dto) {
        ShippingAddress existingAddress = shippingAddressRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shipping address not found with id: " + dto.getId()
                ));

        // Update user if changed
        if (!dto.getUserId().equals(existingAddress.getUser().getId())) {
            User newUser = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "User not found with id: " + dto.getUserId()
                    ));
            existingAddress.setUser(newUser);
        }

        // Update address fields
        existingAddress.setTitle(dto.getTitle());
        existingAddress.setAddressType(ShippingAddress.AddressType.valueOf(dto.getAddressType()));
        existingAddress.setStreet(dto.getStreet());
        existingAddress.setCity(dto.getCity());
        existingAddress.setState(dto.getState());
        existingAddress.setZipCode(dto.getZipCode());
        existingAddress.setCountry(dto.getCountry());

        return shippingAddressRepository.save(existingAddress);
    }

    /**
     * Deletes a shipping address if it is not currently assigned to any order.
     *
     * @param id the ID of the address to delete
     * @throws EntityNotFoundException if the address does not exist
     * @throws IllegalStateException   if the address is used by one or more orders
     */
    public void deleteById(Long id) {
        ShippingAddress address = shippingAddressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shipping address not found with id: " + id
                ));

        // Prevent deletion if address is in use
        List<Order> ordersUsingAddress = orderRepository.findByShippingAddressId(id);
        if (!ordersUsingAddress.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete address as it is being used by " + ordersUsingAddress.size() + " order(s)"
            );
        }

        shippingAddressRepository.delete(address);
    }

    // === Retrieval & Query Methods ===

    /**
     * Retrieves all shipping addresses in the system.
     * <p>
     * Use cautiously in production — may return large datasets.
     * </p>
     *
     * @return a list of all {@link ShippingAddress} entities
     */
    @Transactional(readOnly = true)
    public List<ShippingAddress> findAll() {
        return shippingAddressRepository.findAll();
    }

    /**
     * Retrieves a shipping address by its ID.
     *
     * @param id the unique identifier
     * @return an {@link Optional} containing the address if found
     */
    @Transactional(readOnly = true)
    public Optional<ShippingAddress> findById(Long id) {
        return shippingAddressRepository.findById(id);
    }

    /**
     * Retrieves all shipping addresses associated with a specific user.
     *
     * @param userId the ID of the user
     * @return a list of addresses belonging to the user
     */
    @Transactional(readOnly = true)
    public List<ShippingAddress> findByUserId(Long userId) {
        return shippingAddressRepository.findByUserId(userId);
    }

    /**
     * Retrieves addresses for a user filtered by address type (e.g., HOME, OFFICE).
     *
     * @param userId the ID of the user
     * @param type   the {@link ShippingAddress.AddressType} to filter by
     * @return a list of matching addresses
     */
    @Transactional(readOnly = true)
    public List<ShippingAddress> findByUserIdAndType(Long userId, ShippingAddress.AddressType type) {
        return shippingAddressRepository.findByUserIdAndAddressType(userId, type);
    }

    /**
     * Retrieves the first (oldest) address for a user, useful as a default during checkout.
     *
     * @param userId the ID of the user
     * @return an {@link Optional} containing the first address, if any exist
     */
    @Transactional(readOnly = true)
    public Optional<ShippingAddress> getUserDefaultAddress(Long userId) {
        return shippingAddressRepository.findFirstByUserId(userId);
    }

    /**
     * Checks whether a user has any saved shipping addresses.
     *
     * @param userId the ID of the user
     * @return {@code true} if at least one address exists; {@code false} otherwise
     */
    @Transactional(readOnly = true)
    public boolean userHasAddresses(Long userId) {
        return shippingAddressRepository.existsByUserId(userId);
    }

    /**
     * Counts the number of shipping addresses a user has.
     *
     * @param userId the ID of the user
     * @return the total count of addresses
     */
    @Transactional(readOnly = true)
    public long countUserAddresses(Long userId) {
        return shippingAddressRepository.countByUserId(userId);
    }

    // === Order Integration Methods ===

    /**
     * Assigns a shipping address to an order after validating ownership.
     * <p>
     * Ensures the address belongs to the same user as the order.
     * </p>
     *
     * @param orderId   the ID of the order
     * @param addressId the ID of the address to assign
     * @throws EntityNotFoundException if the order or address is not found
     * @throws IllegalArgumentException  if the address does not belong to the order’s user
     */
    public void assignAddressToOrder(Long orderId, Long addressId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId
                ));

        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shipping address not found with id: " + addressId
                ));

        // Enforce ownership
        if (!address.getUser().getId().equals(order.getUser().getId())) {
            throw new IllegalArgumentException(
                    "Address does not belong to the order's user"
            );
        }

        order.setShippingAddress(address);
        orderRepository.save(order);
    }

    /**
     * Retrieves the shipping address currently assigned to an order.
     *
     * @param orderId the ID of the order
     * @return an {@link Optional} containing the address if assigned
     * @throws EntityNotFoundException if the order does not exist
     */
    @Transactional(readOnly = true)
    public Optional<ShippingAddress> getOrderShippingAddress(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId
                ));

        return Optional.ofNullable(order.getShippingAddress());
    }
}