package com.training.backend.repository;

import com.training.backend.dto.UserProjection;
import com.training.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String username, Long id);

    @Override
    Optional<User> findById(Long aLong);

    @Query(value = """
    SELECT
        u.user_id AS id,
        u.fullname AS fullname,
        u.birthdate AS birthdate,
        d.department_name AS departmentName,
        u.email AS email,
        u.telephone AS telephone,
        c.certification_name AS certificationName,
        uc.end_date AS endDate,
        uc.score AS score
    FROM users u
    JOIN departments d ON u.department_id = d.department_id
    LEFT JOIN (
        SELECT uc1.*
        FROM users_certifications uc1
        JOIN (
            SELECT user_id, MIN(c.certification_level) AS min_level
            FROM users_certifications uc
            JOIN certifications c ON uc.certification_id = c.certification_id
            GROUP BY user_id
        ) uc_min ON uc1.user_id = uc_min.user_id
        JOIN certifications c2 ON uc1.certification_id = c2.certification_id AND c2.certification_level = uc_min.min_level
    ) uc ON u.user_id = uc.user_id
    LEFT JOIN certifications c ON uc.certification_id = c.certification_id
    WHERE (:fullname IS NULL OR :fullname = '' OR u.fullname LIKE %:fullname%)
      AND (:departmentId IS NULL OR :departmentId = '' OR u.department_id = :departmentId)
    ORDER BY
        CASE WHEN :ordFullname = 'DESC' THEN u.fullname END DESC,
        CASE WHEN :ordCertificationName = 'DESC' THEN (c.certification_level IS NULL) END ASC,
        CASE WHEN :ordCertificationName = 'DESC' THEN c.certification_level END ASC,
        CASE WHEN :ordEndDate = 'DESC' THEN uc.end_date END DESC,
        CASE WHEN :ordFullname = 'ASC' THEN u.fullname END ASC,
        CASE WHEN :ordCertificationName = 'ASC' THEN c.certification_level END DESC,
        CASE WHEN :ordEndDate = 'ASC' THEN uc.end_date END ASC,
        u.user_id
    LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<UserProjection> searchUsers(
            @Param("fullname") String fullname,
            @Param("departmentId") String departmentId,
            @Param("ordFullname") String ordFullname,
            @Param("ordCertificationName") String ordCertificationName,
            @Param("ordEndDate") String ordEndDate,
            @Param("offset") String offset,
            @Param("limit") String limit
    );

    @Query(value = """
    SELECT
        u.user_id AS id,
        u.fullname AS fullname,
        u.birthdate AS birthdate,
        d.department_name AS departmentName,
        u.email AS email,
        u.telephone AS telephone,
        c.certification_name AS certificationName,
        uc.end_date AS endDate,
        uc.score AS score
    FROM users u
    JOIN departments d ON u.department_id = d.department_id
    LEFT JOIN (
        SELECT uc1.*
        FROM users_certifications uc1
        JOIN (
            SELECT user_id, MIN(c.certification_level) AS min_level
            FROM users_certifications uc
            JOIN certifications c ON uc.certification_id = c.certification_id
            GROUP BY user_id
        ) uc_min ON uc1.user_id = uc_min.user_id
        JOIN certifications c2 ON uc1.certification_id = c2.certification_id AND c2.certification_level = uc_min.min_level
    ) uc ON u.user_id = uc.user_id
    LEFT JOIN certifications c ON uc.certification_id = c.certification_id
    WHERE (:fullname IS NULL OR :fullname = '' OR u.fullname LIKE %:fullname%)
      AND (:departmentId IS NULL OR :departmentId = '' OR u.department_id = :departmentId)
    """, nativeQuery = true)
    long countUsers(
            @Param("fullname") String fullname,
            @Param("departmentId") String departmentId
    );
}
