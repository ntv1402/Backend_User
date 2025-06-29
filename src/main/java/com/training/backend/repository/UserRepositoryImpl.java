package com.training.backend.repository;

import com.training.backend.dto.UserProjection;
import com.training.backend.entity.Certification;
import com.training.backend.entity.Department;
import com.training.backend.entity.User;
import com.training.backend.entity.UserCerti;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class cho UserRepository sử dụng Criteria Builder
 * Thay thế cho SQL queries truyền thống, cung cấp type-safe và dynamic query building
 */
@Repository
public class UserRepositoryImpl {

    @Autowired
    private EntityManager entityManager;

    /**
     * Tìm kiếm danh sách users với các điều kiện lọc và sắp xếp
     * Sử dụng SQL query đơn giản và map thủ công
     * 
     * @param fullname Tên đầy đủ của user (có thể null hoặc empty)
     * @param departmentId ID của department (có thể null hoặc empty)
     * @param ordFullname Thứ tự sắp xếp theo tên (ASC/DESC/null)
     * @param ordCertificationName Thứ tự sắp xếp theo tên certification (ASC/DESC/null)
     * @param ordEndDate Thứ tự sắp xếp theo ngày kết thúc (ASC/DESC/null)
     * @param offset Vị trí bắt đầu cho pagination (có thể null)
     * @param limit Số lượng records tối đa trả về (có thể null)
     * @return Danh sách UserProjection chứa thông tin users và certifications
     */
    public List<UserProjection> searchUsersWithCriteria(
            String fullname,
            String departmentId,
            String ordFullname,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit) {

        // Xây dựng SQL query
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("u.user_id, ");
        sql.append("u.fullname, ");
        sql.append("u.birthdate, ");
        sql.append("d.department_name, ");
        sql.append("u.email, ");
        sql.append("u.telephone ");
        sql.append("FROM users u ");
        sql.append("JOIN departments d ON u.department_id = d.department_id ");
        sql.append("WHERE 1=1 ");

        // Thêm điều kiện WHERE
        if (fullname != null && !fullname.isEmpty()) {
            sql.append("AND u.fullname LIKE :fullname ");
        }
        if (departmentId != null && !departmentId.isEmpty()) {
            sql.append("AND u.department_id = :departmentId ");
        }

        // Thêm ORDER BY
        sql.append("ORDER BY ");
        if ("DESC".equals(ordFullname)) {
            sql.append("u.fullname DESC, ");
        } else if ("ASC".equals(ordFullname)) {
            sql.append("u.fullname ASC, ");
        }
        sql.append("u.user_id ASC ");

        // Thêm LIMIT và OFFSET
        if (limit != null) {
            sql.append("LIMIT :limit ");
        }
        if (offset != null) {
            sql.append("OFFSET :offset");
        }

        // Tạo query
        jakarta.persistence.Query query = entityManager.createNativeQuery(sql.toString());

        // Set parameters
        if (fullname != null && !fullname.isEmpty()) {
            query.setParameter("fullname", "%" + fullname + "%");
        }
        if (departmentId != null && !departmentId.isEmpty()) {
            query.setParameter("departmentId", departmentId);
        }
        if (limit != null) {
            query.setParameter("limit", limit);
        }
        if (offset != null) {
            query.setParameter("offset", offset);
        }

        // Thực thi query và map kết quả
        List<Object[]> results = query.getResultList();
        List<UserProjection> projections = new ArrayList<>();
        
        for (Object[] row : results) {
            UserProjection projection = new UserProjection() {
                @Override
                public Long getUserId() {
                    return row[0] != null ? ((Number) row[0]).longValue() : null;
                }

                @Override
                public String getFullname() {
                    return (String) row[1];
                }

                @Override
                public String getEmail() {
                    return (String) row[4];
                }

                @Override
                public LocalDate getBirthDate() {
                    return row[2] != null ? (LocalDate) row[2] : null;
                }

                @Override
                public String getDepartmentName() {
                    return (String) row[3];
                }

                @Override
                public String getTelephone() {
                    return (String) row[5];
                }

                @Override
                public String getCertificationName() {
                    return null; // Không có certification
                }

                @Override
                public LocalDate getEndDate() {
                    return null; // Không có certification
                }

                @Override
                public BigDecimal getScore() {
                    return null; // Không có certification
                }
            };
            projections.add(projection);
        }

        return projections;
    }

    /**
     * Đếm tổng số lượng users thỏa mãn điều kiện lọc
     * Sử dụng Criteria Builder để tạo COUNT query
     * 
     * @param fullname Tên đầy đủ của user (có thể null hoặc empty)
     * @param departmentId ID của department (có thể null hoặc empty)
     * @return Tổng số lượng users thỏa mãn điều kiện
     */
    public long countUsersWithCriteria(String fullname, String departmentId) {
        // Tạo CriteriaBuilder cho COUNT query
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        // Khai báo root entities cần thiết
        Root<User> userRoot = query.from(User.class);
        Root<Department> departmentRoot = query.from(Department.class);

        // Xây dựng SELECT COUNT clause
        query.select(cb.countDistinct(userRoot.get("userId")));

        // Xây dựng WHERE clause với các điều kiện lọc
        List<Predicate> predicates = new ArrayList<>();

        // Điều kiện JOIN giữa User và Department
        predicates.add(cb.equal(userRoot.get("departmentId"), departmentRoot.get("departmentId")));

        // Thêm điều kiện lọc theo tên (LIKE search)
        if (fullname != null && !fullname.isEmpty()) {
            predicates.add(cb.like(userRoot.get("fullname"), "%" + fullname + "%"));
        }

        // Thêm điều kiện lọc theo department
        if (departmentId != null && !departmentId.isEmpty()) {
            predicates.add(cb.equal(userRoot.get("departmentId"), departmentId));
        }

        // Áp dụng tất cả điều kiện WHERE vào query
        query.where(predicates.toArray(new Predicate[0]));

        // Thực thi query và trả về kết quả
        return entityManager.createQuery(query).getSingleResult();
    }
} 