package com.app.DTO;

import com.app.entities.User;
import lombok.Getter;
import lombok.Setter;

// DTO para el registro
@Getter
@Setter
public class RegisterDTO {
    private String username;
    private String email;
    private String password;
    private String avatar;
    private User.Role role;

}
