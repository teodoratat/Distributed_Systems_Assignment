package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.UserDetailsDTO;
import ro.tuc.ds2020.dtos.builders.UserBuilder;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }


    public List<UserDTO> findUsersForAdmin() {
        List<User> usersList = userRepository.findUserByRole("user"); // Assuming you have a 'role' field in your User entity
        return usersList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> findAdminsForUser() {
        List<User> usersList = userRepository.findUserByRole("admin"); // Assuming you have a 'role' field in your User entity
        return usersList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }
    public List<UserDTO> findUsersByRoleOfUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if ("user".equals(user.getRole())) {
                // If the role is 'user', retrieve all admins
                List<User> adminsList = userRepository.findUserByRole("admin");
                return adminsList.stream()
                        .map(UserBuilder::toUserDTO)
                        .collect(Collectors.toList());
            } else if ("admin".equals(user.getRole())) {
                // If the role is 'admin', retrieve all users
                List<User> usersList = userRepository.findUserByRole("user");
                return usersList.stream()
                        .map(UserBuilder::toUserDTO)
                        .collect(Collectors.toList());
            } else {
                // Handle other roles or throw an exception for invalid roles
                throw new IllegalArgumentException("Invalid role: " + user.getRole());
            }
        } else {
            // Handle the case when the user is not found
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }
    }

    public UserDetailsDTO findUserById(UUID id) {
        Optional<User> prosumerOptional = userRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDetailsDTO(prosumerOptional.get());
    }
    public UserDetailsDTO findUserByUsernameAndPassword(String username, String password)
    {
        Optional<User> user = userRepository.findUserByUsernameAndPassword(username,password);
        if(!user.isPresent()){
            LOGGER.error("User with username {} was not found in db, incorrect username or password!", username);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + username);
        }
        return UserBuilder.toUserDetailsDTO(user.get());
    }

    /*public UUID insert(UserDetailsDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());
        return user.getId();
    }*/
    public UUID insertUser(UserDetailsDTO userDTO) {
        UUID uuid = UUID. randomUUID();
        userDTO.setId(uuid);
        User user = UserBuilder.toEntityUpdate(userDTO);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());
        return user.getId();
    }
   /* public UUID update(UUID id, UserDetailsDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        User existingUser = userOptional.get();

        // Use UserBuilder to update the fields of the existing user
        User updatedUser = UserBuilder.toEntityUpdate(userDTO);
        updatedUser.setId(existingUser.getId()); // Ensure the ID remains the same

        // Save the updated user
        updatedUser = userRepository.save(updatedUser);
        LOGGER.debug("User with id {} was updated in db", updatedUser.getId());
        return updatedUser.getId();
    }*/


    public boolean delete(UUID id) {
        try {
            userRepository.deleteById(id);
            LOGGER.debug("User was deleted in db");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /*public UserDTO updateD1(UUID id, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in the database", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        User existingUser = userOptional.get();
        User updatedUser = UserBuilder.toEntity1(userDTO);

        updatedUser.setId(existingUser.getId());

        User savedUser = userRepository.save(updatedUser);

        LOGGER.debug("User with id {} was updated in the database", id);
        return UserBuilder.toUserDTO(savedUser);
    }*/

    public UserDetailsDTO update(UUID id, UserDetailsDTO userDetailsDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in the database", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        User existingUser = userOptional.get();
        User updatedUser = UserBuilder.toEntityUpdate(userDetailsDTO);

        updatedUser.setId(existingUser.getId());

        User savedUser = userRepository.save(updatedUser);

        LOGGER.debug("User with id {} was updated in the database", id);
        return UserBuilder.toUserDetailsDTO(savedUser);
    }

}
