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

@RestController
@RequestMapping("/api/shippingAddresses")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    // =============== CREATE NEW ADDRESS ===============
    @PostMapping
    public ResponseEntity<?> createShippingAddress(@Valid @RequestBody ShippingAddressDTO dto) {
        try {
            ShippingAddress created = shippingAddressService.createShippingAddress(dto);
            ShippingAddressDTO response = convertToDTO(created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating address: " + e.getMessage()));
        }
    }

    // =============== GET ALL ADDRESSES ===============
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

    // =============== GET ADDRESSES BY USER ===============
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
                    .body(Map.of("error", "Error searching user addresses"));
        }
    }

    // =============== GET ADDRESS BY ID ===============
    @GetMapping("/{id}")
    public ResponseEntity<?> getShippingAddressById(@PathVariable Long id) {
        try {
            Optional<ShippingAddress> address = shippingAddressService.findById(id);

            if (address.isPresent()) {
                return ResponseEntity.ok(convertToDTO(address.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error searching address"));
        }
    }

    // =============== GET ADDRESS OF AN ORDER ===============
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderShippingAddress(@PathVariable Long orderId) {
        try {
            Optional<ShippingAddress> address = shippingAddressService.getOrderShippingAddress(orderId);

            if (address.isPresent()) {
                return ResponseEntity.ok(convertToDTO(address.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error searching address"));
        }
    }

    // =============== UPDATE ADDRESS ===============
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShippingAddress(@PathVariable Long id,
                                                   @Valid @RequestBody ShippingAddressDTO dto) {
        try {
            dto.setId(id);
            ShippingAddress updated = shippingAddressService.updateShippingAddress(dto);
            return ResponseEntity.ok(convertToDTO(updated));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Address not found with ID: " + id));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating address: " + e.getMessage()));
        }
    }

    // =============== ASSIGN ADDRESS TO ORDER ===============
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

    // =============== DELETE ADDRESS ===============
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

    // =============== GET DEFAULT ADDRESS OF USER ===============
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
                    .body(Map.of("error", "Error searching default address"));
        }
    }

    // =============== PRIVATE METHOD FOR CONVERSION ===============
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
