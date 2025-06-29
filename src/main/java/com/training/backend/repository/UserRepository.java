package com.training.backend.repository;

import com.training.backend.dto.UserProjection;
import com.training.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho User entity
 * Cung cấp các phương thức truy vấn cơ bản và tìm kiếm nâng cao
 * Sử dụng SQL queries truyền thống thay vì Criteria Builder
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    /**
     * Tìm user theo username
     * 
     * @param username Tên đăng nhập cần tìm
     * @return Optional chứa User nếu tìm thấy, empty nếu không tìm thấy
     */
    Optional<User> findByUsername(String username);

    /**
     * Kiểm tra xem username đã tồn tại trong hệ thống chưa
     * 
     * @param username Tên đăng nhập cần kiểm tra
     * @return true nếu username đã tồn tại, false nếu chưa
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra xem email đã tồn tại trong hệ thống chưa
     * 
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra xem email đã tồn tại bởi user khác chưa (dùng cho update)
     * 
     * @param username Email cần kiểm tra
     * @param userId ID của user hiện tại (để loại trừ)
     * @return true nếu email đã được sử dụng bởi user khác, false nếu chưa
     */
    boolean existsByEmailAndUserIdNot(String username, Long userId);

    /**
     * Tìm user theo ID
     * Override method từ JpaRepository
     * 
     * @param userId ID của user cần tìm
     * @return Optional chứa User nếu tìm thấy, empty nếu không tìm thấy
     */
    @Override
    Optional<User> findById(Long userId);

    /**
     * Tìm kiếm danh sách users với các điều kiện lọc và sắp xếp
     * Sử dụng SQL query phức tạp với JOIN và subquery
     * 
     * @param fullname Tên đầy đủ để tìm kiếm (có thể null hoặc empty)
     * @param departmentId ID department để lọc (có thể null hoặc empty)
     * @param ordFullname Thứ tự sắp xếp theo tên (ASC/DESC/null)
     * @param ordCertificationName Thứ tự sắp xếp theo tên certification (ASC/DESC/null)
     * @param ordEndDate Thứ tự sắp xếp theo ngày kết thúc (ASC/DESC/null)
     * @param offset Vị trí bắt đầu cho pagination
     * @param limit Số lượng records tối đa trả về
     * @return Danh sách UserProjection chứa thông tin users và certifications
     */
    @Query(value = """
            SELECT
                u.user_id AS userId,
                u.fullname AS fullname,
                u.birthdate AS birthDate,
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
                FROM user_certification uc1
                JOIN (
                    SELECT user_id, MIN(c.certification_level) AS min_level
                    FROM user_certification uc
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
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * Đếm tổng số lượng users thỏa mãn điều kiện lọc
     * Sử dụng SQL query tương tự searchUsers nhưng chỉ đếm số lượng
     * 
     * @param fullname Tên đầy đủ để tìm kiếm (có thể null hoặc empty)
     * @param departmentId ID department để lọc (có thể null hoặc empty)
     * @return Tổng số lượng users thỏa mãn điều kiện
     */
    @Query(value = """
            SELECT COUNT(DISTINCT u.user_id)
            FROM users u
            JOIN departments d ON u.department_id = d.department_id
            LEFT JOIN (
                SELECT uc1.*
                FROM user_certification uc1
                JOIN (
                    SELECT user_id, MIN(c.certification_level) AS min_level
                    FROM user_certification uc
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
