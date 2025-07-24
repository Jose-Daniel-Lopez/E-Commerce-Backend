package com.app.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPatchDTO {

    private String username;
    private String location;
    private String avatar;
    // Removed password field - password changes should only be done through ChangePasswordDTO
}
