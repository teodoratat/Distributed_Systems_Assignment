package com.bezkoder.spring.login.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "user")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(name = "username", nullable = false, length = 20, unique = true)
  private String username;

  @Column(name = "password", nullable = false, length = 100)
  private String password;

  @Column(name = "role", nullable = false)
  private String role;




  public User() {
  }

  public User(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;

  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }



  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }




  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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
}

//@Entity
//@Table(name = "users",
//       uniqueConstraints = {
//           @UniqueConstraint(columnNames = "username"),
//           @UniqueConstraint(columnNames = "email")
//       })
//public class User {
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;
//
//  @NotBlank
//  @Size(max = 20)
//  private String username;
//
//  @NotBlank
//  @Size(max = 50)
//  @Email
//  private String email;
//
//  @NotBlank
//  @Size(max = 120)
//  private String password;
//
//  @ManyToMany(fetch = FetchType.LAZY)
//  @JoinTable(name = "user_roles",
//             joinColumns = @JoinColumn(name = "user_id"),
//             inverseJoinColumns = @JoinColumn(name = "role_id"))
//  private Set<Role> roles = new HashSet<>();
//
//  public User() {
//  }
//
//  public User(String username, String email, String password) {
//    this.username = username;
//    this.email = email;
//    this.password = password;
//  }
//
//  public User(Long id, String username, String email, String password, Set<Role> roles) {
//    this.id = id;
//    this.username = username;
//    this.email = email;
//    this.password = password;
//    this.roles = roles;
//  }
//
//  public Long getId() {
//    return id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }
//
//  public String getUsername() {
//    return username;
//  }
//
//  public void setUsername(String username) {
//    this.username = username;
//  }
//
//  public String getEmail() {
//    return email;
//  }
//
//  public void setEmail(String email) {
//    this.email = email;
//  }
//
//  public String getPassword() {
//    return password;
//  }
//
//  public void setPassword(String password) {
//    this.password = password;
//  }
//
//  public Set<Role> getRoles() {
//    return roles;
//  }
//
//  public void setRoles(Set<Role> roles) {
//    this.roles = roles;
//  }
//}
