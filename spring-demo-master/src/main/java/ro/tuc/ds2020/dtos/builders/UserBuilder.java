package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.UserDetailsDTO;
import ro.tuc.ds2020.entities.User;

public class UserBuilder {

    private UserBuilder() {
    }

    public static UserDTO toUserDTO(User person) {
        return new UserDTO(person.getId(), person.getUsername(), person.getPassword(), person.getRole());
    }
    public static UserDTO toDtoForDevice(User user){
        return new UserDTO(user.getId(), user.getUsername());
    }

    public static UserDetailsDTO toUserDetailsDTO(User person) {
        return new UserDetailsDTO(person.getId(), person.getUsername(), person.getPassword(), person.getRole());
    }

    public static User toEntity(UserDetailsDTO userDetailsDTO) {
        System.out.println("eu incerc da nu prea reusesc builder");
        return new User(
                userDetailsDTO.getUsername(),
                userDetailsDTO.getPassword(),
                userDetailsDTO.getRole()
        );
    }
    public static User toEntity1(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getUsername(), userDTO.getPassword(),userDTO.getRole());
    }
    public static User toEntityUpdate(UserDetailsDTO userDetailsDTO) {
        return new User(
                userDetailsDTO.getId(),
                userDetailsDTO.getUsername(),
                userDetailsDTO.getPassword(),
                userDetailsDTO.getRole()
        );
    }
}
