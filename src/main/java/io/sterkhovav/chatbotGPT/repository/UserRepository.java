package io.sterkhovav.chatbotGPT.repository;

import io.sterkhovav.chatbotGPT.enums.ModelGPTEnum;
import io.sterkhovav.chatbotGPT.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u.modelGPT FROM User u WHERE u.username = :username")
    ModelGPTEnum findModelGPTByUsername(@Param("username") String username);
}
