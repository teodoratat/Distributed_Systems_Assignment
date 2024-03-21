package com.bezkoder.spring.login.security.services;

import com.bezkoder.spring.login.dtos.UserDTO;
import com.bezkoder.spring.login.dtos.builders.UserBuilder;
import com.bezkoder.spring.login.models.User;
import com.bezkoder.spring.login.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findUsers() {
        List<User> userList = userRepository.findAll();
        System.out.println(userList);
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findUserByID(UUID id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new Exception(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDTO(userOptional.get());
    }

    public UserDTO updateUser(UUID id, UserDTO updateUserDTO) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(updateUserDTO.getUsername());
            user.setRole(updateUserDTO.getRole());

            User updatedUser = userRepository.save(user);

            return UserBuilder.toUserDTO(updatedUser);
        } else {
            return null;
        }
    }

    public void deleteUserById(UUID id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new Exception(User.class.getSimpleName() + " with id: " + id);
        }
        else{
            userRepository.deleteById(id);
//            restTemplate.delete(deviceManagementBaseUrl+ "/device/user/delete/" + id);
        }
    }
    public List<UserDTO> findUsersByRoleForUser(UUID userId) throws Exception {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new Exception(User.class.getSimpleName() + " with id: " + userId + " not found");
        }

        String userRole = userOptional.get().getRole();
        List<User> userList;

        if ("admin".equals(userRole)) {
            // If the specified user is an admin, retrieve all users excluding the specified user
            userList = userRepository.findByRoleAndIdNot("user", userId);
        } else {
            // If the specified user is a user, retrieve all admins excluding the specified user
            userList = userRepository.findByRoleAndIdNot("admin", userId);
        }

        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }
}
