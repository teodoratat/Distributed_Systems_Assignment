package com.bezkoder.spring.login.dtos;



import java.util.Objects;
import java.util.UUID;

public class UserDTO {

    private UUID id;
    private String username;
    private String password;
    private String role;


    public UserDTO() {
    }

    public UserDTO(UUID id, String username, String password,String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;

    }

    public UserDTO(UUID id, String username) {
        this.id = id;
        this.username= username;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;
        if (!super.equals(o)) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(username, userDTO.username) &&
                Objects.equals(password, userDTO.password) &&
                Objects.equals(role, userDTO.role);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username,password, role);
    }
}
