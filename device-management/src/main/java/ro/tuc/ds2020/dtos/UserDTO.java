package ro.tuc.ds2020.dtos;


import ro.tuc.ds2020.entities.Device;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


public class UserDTO {
    private UUID id;
    @NotNull
    private String username;

    public UserDTO(UUID id, String username) {
        this.id = id;
        this.username = username;
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

    public UserDTO(){

    }
}