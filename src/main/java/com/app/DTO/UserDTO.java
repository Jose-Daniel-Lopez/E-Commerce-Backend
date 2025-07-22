package com.app.DTO;

import com.app.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String location;
    private String avatar;
    private String role;
    private boolean isVerified;

    // Constructors
    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.location = user.getLocation();
        this.avatar = user.getAvatar();
        this.role = user.getRole().name();
        this.isVerified = user.isVerified();
    }
}
