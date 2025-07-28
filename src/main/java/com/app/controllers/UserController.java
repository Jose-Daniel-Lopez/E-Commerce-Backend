package com.app.controllers;

import com.app.DTO.ChangePasswordDTO;
import com.app.DTO.UserPatchDTO;
import com.app.entities.User;
import com.app.hateoas.UserRepresentation;
import com.app.hateoas.HateoasLinkBuilder;
import com.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing user-related operations.
 * <p>
 * Provides endpoints for retrieving, updating, and modifying user data,
 * including password changes. Integrates HATEOAS to return enriched
 * {@link UserRepresentation} responses with navigational links.
 * </p>
 * <p>
 * Base URL: {@code /api/users}
 * </p>
 * <p>
 * Security: Most endpoints require authentication. Password changes are restricted
 * to the user themselves or an admin.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final HateoasLinkBuilder hateoasLinkBuilder;

    /**
     * Constructs a new UserController with required dependencies.
     *
     * @param userService        the service handling user business logic
     * @param hateoasLinkBuilder builds HATEOAS-compliant representations with navigational links
     */
    @Autowired
    public UserController(UserService userService, HateoasLinkBuilder hateoasLinkBuilder) {
        this.userService = userService;
        this.hateoasLinkBuilder = hateoasLinkBuilder;
    }

    // === READ ENDPOINTS ===

    /**
     * Retrieves a specific user by ID and returns a HATEOAS-enriched representation.
     *
     * @param id the unique identifier of the user
     * @return 200 OK with {@link UserRepresentation} if found; 404 if not found
     * @response 200 Successfully returns the user with links
     * @response 404 User not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserRepresentation> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserRepresentation representation = hateoasLinkBuilder.buildUserRepresentation(user);
        return ResponseEntity.ok(representation);
    }

    /**
     * Retrieves the currently authenticated user.
     * <p>
     * This endpoint is typically used by the frontend to fetch the logged-in user's profile.
     * </p>
     *
     * @param user the authenticated user (injected by Spring Security)
     * @return 200 OK with user data; 401 if not authenticated
     * @response 200 Successfully returns current user
     * @response 401 User not authenticated
     */
    @GetMapping("/me")
    public ResponseEntity<UserRepresentation> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserRepresentation representation = hateoasLinkBuilder.buildUserRepresentation(user);
        return ResponseEntity.ok(representation);
    }

    // === UPDATE ENDPOINTS ===

    /**
     * Partially updates a user's profile using a patch request.
     * <p>
     * Only the fields provided in the {@link UserPatchDTO} will be updated.
     * </p>
     *
     * @param id       the ID of the user to update
     * @param patchDTO the partial user data to apply
     * @return 200 OK with updated {@link UserRepresentation}, or 404 if user not found
     * @response 200 Successfully updated user
     * @response 404 User not found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserRepresentation> patchUser(
            @PathVariable Long id,
            @RequestBody UserPatchDTO patchDTO) {

        User updatedUser = userService.patchUser(id, patchDTO);
        UserRepresentation representation = hateoasLinkBuilder.buildUserRepresentation(updatedUser);
        return ResponseEntity.ok(representation);
    }

    /**
     * Changes the password for a specific user.
     * <p>
     * Only the user themselves or an admin can change the password.
     * Requires current password verification and new password confirmation.
     * </p>
     *
     * @param id                 the ID of the user whose password is being changed
     * @param request            the change password data (current, new, confirm)
     * @param authenticatedUser  the currently logged-in user (from security context)
     * @return 200 OK on success, 400 on invalid input, 403 if unauthorized, 500 on error
     * @response 200 Password changed successfully
     * @response 400 Invalid current password or mismatched new passwords
     * @response 403 Insufficient permissions (not self or admin)
     * @response 500 Internal server error
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordDTO request,
            @AuthenticationPrincipal User authenticatedUser) {

        try {
            // Authorization check: must be self or admin
            if (!authenticatedUser.getId().equals(id) && !authenticatedUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only change your own password"));
            }

            // Retrieve the target user
            User targetUser = userService.getUserById(id);
            if (targetUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            // Delegate password change logic to service (includes validation)
            userService.changePassword(targetUser, request);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while changing password"));
        }
    }
}