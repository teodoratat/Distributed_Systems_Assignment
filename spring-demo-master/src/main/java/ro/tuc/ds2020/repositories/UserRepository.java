package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
    List<User> findByUsername(String username);


    /**
     * Example: Write Custom Query
     */
    @Query(value = "SELECT p " +
            "FROM User p " +
            "WHERE p.username = :username ")
    Optional<User> findSeniorsByUsername(@Param("username") String username);

    Optional<User> findUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    List<User>findUserByRole(String role);


}
