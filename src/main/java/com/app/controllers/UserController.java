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

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final HateoasLinkBuilder hateoasLinkBuilder;

    @Autowired
    public UserController(UserService userService, HateoasLinkBuilder hateoasLinkBuilder) {
        this.userService = userService;
        this.hateoasLinkBuilder = hateoasLinkBuilder;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRepresentation> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Convert to HATEOAS representation
        UserRepresentation userRepresentation = hateoasLinkBuilder.buildUserRepresentation(user);
        return ResponseEntity.ok(userRepresentation);
    }

    @GetMapping("/me")
    public ResponseEntity<UserRepresentation> getCurrentUser(@AuthenticationPrincipal User user) {
        // Returns the current authenticated user with HATEOAS links
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserRepresentation userRepresentation = hateoasLinkBuilder.buildUserRepresentation(user);
        return ResponseEntity.ok(userRepresentation);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserRepresentation> patchUser(
            @PathVariable Long id,
            @RequestBody UserPatchDTO patchDTO) {
        User updatedUser = userService.patchUser(id, patchDTO);
        // Convert to HATEOAS representation
        UserRepresentation userRepresentation = hateoasLinkBuilder.buildUserRepresentation(updatedUser);
        return ResponseEntity.ok(userRepresentation);
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordDTO request,
            @AuthenticationPrincipal User authenticatedUser) {

        try {
            // Verify that the authenticated user is changing their own password or is an admin
            if (!authenticatedUser.getId().equals(id) && !authenticatedUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only change your own password"));
            }

            // Get the user whose password will be changed
            User targetUser = userService.getUserById(id);
            userService.changePassword(targetUser, request);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while changing password"));
        }
    }
}
