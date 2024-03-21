package com.bezkoder.spring.login.controllers;

import com.bezkoder.spring.login.dtos.UserDTO;
import com.bezkoder.spring.login.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@CrossOrigin(origins="http://localhost:3000", maxAge = 3600,allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('user') or  hasAuthority('admin')")
    public String userAccess() {
        return "User Content.";
    }


    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('user') or  hasAuthority('admin')")
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> dtos = userService.findUsers();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    @GetMapping("/allUsers/{id}")
    @PreAuthorize("hasAuthority('user') or  hasAuthority('admin')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable("id") UUID userID) throws Exception {
        List<UserDTO> dto = userService.findUsersByRoleForUser(userID);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('user') or  hasAuthority('admin')")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") UUID userID) throws Exception {
        UserDTO dto = userService.findUserByID(userID);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('user') or  hasAuthority('admin')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO updatedUserDTO) {
        UserDTO updatedUser = userService.updateUser(id, updatedUserDTO);

        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('user') or  hasAuthority('admin')")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        try {
            userService.deleteUserById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}