package com.app.controllers;

import com.app.DTO.UserDTO;
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
}
