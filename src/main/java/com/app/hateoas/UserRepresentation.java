package com.app.hateoas;

import com.app.entities.User;
import org.springframework.hateoas.RepresentationModel;

public class UserRepresentation extends RepresentationModel<UserRepresentation> {

    private Long id;
    private String username;
    private String email;
    private String location;
    private String avatar;
    private User.Role role;
    private boolean isVerified;

    public UserRepresentation() {}

    public UserRepresentation(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.location = user.getLocation();
        this.avatar = user.getAvatar();
        this.role = user.getRole();
        this.isVerified = user.isVerified();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}
