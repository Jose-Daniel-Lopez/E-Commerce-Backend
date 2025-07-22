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
 * Service class for managing shipping addresses.
 * Handles CRUD operations and business logic for user shipping addresses.
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

    /**
     * Creates a new shipping address for a user.
     *
     * @param dto the shipping address data transfer object
     * @return the created shipping address
     * @throws EntityNotFoundException if the user is not found
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
     * Retrieves all shipping addresses in the system.
     *
     * @return list of all shipping addresses
     */
    @Transactional(readOnly = true)
    public List<ShippingAddress> findAll() {
        return shippingAddressRepository.findAll();
    }

    /**
     * Finds a shipping address by its ID.
     *
     * @param id the address ID
     * @return optional containing the address if found
     */
    @Transactional(readOnly = true)
    public Optional<ShippingAddress> findById(Long id) {
        return shippingAddressRepository.findById(id);
    }

    /**
     * Retrieves all shipping addresses for a specific user.
     *
     * @param userId the user ID
     * @return list of addresses belonging to the user
     */
    @Transactional(readOnly = true)
    public List<ShippingAddress> findByUserId(Long userId) {
        return shippingAddressRepository.findByUserId(userId);
    }

    /**
     * Finds addresses for a user filtered by address type.
     *
     * @param userId the user ID
     * @param type   the address type (HOME, OFFICE, PICKUP)
     * @return list of addresses matching the criteria
     */
    @Transactional(readOnly = true)
    public List<ShippingAddress> findByUserIdAndType(Long userId, ShippingAddress.AddressType type) {
        return shippingAddressRepository.findByUserIdAndAddressType(userId, type);
    }

    /**
     * Updates an existing shipping address.
     *
     * @param dto the updated address data
     * @return the updated shipping address
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
     * Assigns a shipping address to an order.
     * Validates that the address belongs to the same user as the order.
     *
     * @param orderId   the order ID
     * @param addressId the address ID
     * @throws EntityNotFoundException    if order or address is not found
     * @throws IllegalArgumentException if address doesn't belong to order's user
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

        // Validate ownership
        if (!address.getUser().getId().equals(order.getUser().getId())) {
            throw new IllegalArgumentException(
                    "Address does not belong to the order's user"
            );
        }

        order.setShippingAddress(address);
        orderRepository.save(order);
    }

    /**
     * Retrieves the shipping address associated with an order.
     *
     * @param orderId the order ID
     * @return optional containing the shipping address if assigned
     * @throws EntityNotFoundException if the order is not found
     */
    @Transactional(readOnly = true)
    public Optional<ShippingAddress> getOrderShippingAddress(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId
                ));

        return Optional.ofNullable(order.getShippingAddress());
    }

    /**
     * Deletes a shipping address by ID.
     * Prevents deletion if the address is currently used by any orders.
     *
     * @param id the address ID
     * @throws EntityNotFoundException if the address is not found
     * @throws IllegalStateException   if the address is used by existing orders
     */
    public void deleteById(Long id) {
        ShippingAddress address = shippingAddressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shipping address not found with id: " + id
                ));

        // Check if address is in use
        List<Order> ordersUsingAddress = orderRepository.findByShippingAddressId(id);
        if (!ordersUsingAddress.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete address as it is being used by " + ordersUsingAddress.size() + " order(s)"
            );
        }

        shippingAddressRepository.delete(address);
    }

    /**
     * Checks if a user has any shipping addresses.
     *
     * @param userId the user ID
     * @return true if user has addresses, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean userHasAddresses(Long userId) {
        return shippingAddressRepository.existsByUserId(userId);
    }

    /**
     * Counts the number of addresses for a user.
     *
     * @param userId the user ID
     * @return the count of user's addresses
     */
    @Transactional(readOnly = true)
    public long countUserAddresses(Long userId) {
        return shippingAddressRepository.countByUserId(userId);
    }

    /**
     * Retrieves the first/default address for a user.
     * Useful for auto-selecting an address during checkout.
     *
     * @param userId the user ID
     * @return optional containing the first address if any exist
     */
    @Transactional(readOnly = true)
    public Optional<ShippingAddress> getUserDefaultAddress(Long userId) {
        return shippingAddressRepository.findFirstByUserId(userId);
    }
}
