package io.sterkhovav.chatbotGPT.repository;

import io.sterkhovav.chatbotGPT.models.UserDialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserDialogRepository extends JpaRepository<UserDialog, Long> {

    @Query(value = "SELECT * FROM gpt_users_dialogs ud WHERE ud.user_id = :userId ORDER BY ud.create_date ASC LIMIT :limit", nativeQuery = true)
    List<UserDialog> findByUserIdWithLimit(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );


}
