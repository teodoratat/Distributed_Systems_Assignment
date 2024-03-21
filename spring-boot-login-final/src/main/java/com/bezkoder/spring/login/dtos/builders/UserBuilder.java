package com.bezkoder.spring.login.dtos.builders;

import com.bezkoder.spring.login.dtos.UserDTO;
import com.bezkoder.spring.login.models.User;


public class UserBuilder {

    private UserBuilder() {
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }

    public static User toEntity(UserDTO userDTO) {
        return new User(userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getRole());
    }

    public static UserDTO toDtoForDevice(User user){
        return new UserDTO(user.getId(), user.getUsername());
    }

}
