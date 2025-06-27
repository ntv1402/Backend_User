package com.training.backend.repository;

import com.training.backend.entity.UserCerti;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCertiRepository extends JpaRepository<UserCerti,Long> {
    @Query("SELECT uc FROM UserCerti uc WHERE uc.userId = :userId")
    List<UserCerti> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserCerti uc WHERE uc.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
