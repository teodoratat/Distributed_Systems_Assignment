package ro.tuc.ds2020.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.UserDetailsDTO;
import ro.tuc.ds2020.dtos.builders.UserBuilder;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> dtos = userService.findUsers();
        for (UserDTO dto : dtos) {
            Link userLink = linkTo(methodOn(UserController.class)
                    .getUser(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<UserDTO>> findUsersForAdmin() {
        List<UserDTO> dtos = userService.findUsersForAdmin();
        for (UserDTO dto : dtos) {
            Link userLink = linkTo(methodOn(UserController.class)
                    .getUser(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    @GetMapping("/role/{userId}")
    public ResponseEntity<List<UserDTO>> findUsersByRoleOfUser(@PathVariable UUID userId) {
        try {
            List<UserDTO> dtos = userService.findUsersByRoleOfUser(userId);

            for (UserDTO dto : dtos) {
                Link userLink = linkTo(methodOn(UserController.class)
                        .getUser(dto.getId())).withRel("userDetails");
                dto.add(userLink);
            }

            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/findAdmins")
    public ResponseEntity<List<UserDTO>> findAdmins() {
        List<UserDTO> dtos = userService.findAdminsForUser();
        for (UserDTO dto : dtos) {
            Link userLink = linkTo(methodOn(UserController.class)
                    .getUser(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    @PostMapping()
    public ResponseEntity<UUID> insert(@Valid @RequestBody UserDetailsDTO userDetailsDTO){
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("am intrat"+userDetailsDTO);
        userService.insertUser(userDetailsDTO);
        UUID userId = userDetailsDTO.getId();
        System.out.println("id u ii"+ userId);
        User user = UserBuilder.toEntityUpdate(userDetailsDTO);
        UserDTO userDTO = UserBuilder.toUserDTO(user);
            // URL to communicate from spring-demo1 to spring-demo2
        //ResponseEntity<UserDTO> response = restTemplate.postForEntity("http://spring-demo2:8082/user", userDTO, UserDTO.class);
        System.out.println("alo alo");
        ResponseEntity<UserDTO> response = restTemplate.postForEntity("http://localhost:8082/user", userDTO, UserDTO.class);
        System.out.println("Response from other service: " + response);
        return new ResponseEntity<> (userId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> updateUser(@PathVariable("id") UUID userId, @Valid @RequestBody UserDetailsDTO userDetailsDTO) {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("aaaaaa");
        UserDetailsDTO userID = userService.update(userId,userDetailsDTO);
        System.out.println("am iesit pa");
        User user = UserBuilder.toEntityUpdate(userDetailsDTO);
        System.out.println(user);
        UserDTO userDTO = UserBuilder.toDtoForDevice(user);
        System.out.println(userDTO);
        //ResponseEntity<UserDTO> response = restTemplate.postForEntity("http://spring-demo2:8082/user", userDTO, UserDTO.class);
        ResponseEntity<UserDTO> response = restTemplate.postForEntity("http://localhost:8082/user", userDTO, UserDTO.class);
        return new ResponseEntity<>(userID.getId(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDetailsDTO> getUser(@PathVariable("id") UUID userId) {
        UserDetailsDTO dto = userService.findUserById(userId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<UserDetailsDTO> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        UserDetailsDTO dto = userService.findUserByUsernameAndPassword(username, password);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID userId) {
        RestTemplate restTemplate = new RestTemplate();
        // Check if the user exists before attempting to delete
        //UserDetailsDTO userToDelete = restTemplate.getForObject("http://spring-demo2:8082/user/{id}", UserDetailsDTO.class, userId);
        UserDetailsDTO userToDelete = restTemplate.getForObject("http://localhost:8082/user/{id}", UserDetailsDTO.class, userId);
        if (userToDelete != null) {
            //restTemplate.delete("http://spring-demo2:8082/user/{id}", userId);
            restTemplate.delete("http://localhost:8082/user/{id}", userId);
            userService.delete(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // Deletion was unsuccessful, user not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
