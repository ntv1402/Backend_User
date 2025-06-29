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

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl {

    @Autowired
    private EntityManager entityManager;

    /**
     * Tìm kiếm users với criteria builder
     */
    public List<UserProjection> searchUsersWithCriteria(
            String fullname,
            String departmentId,
            String ordFullname,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserProjection> query = cb.createQuery(UserProjection.class);

        // Root entities
        Root<User> userRoot = query.from(User.class);
        Root<Department> departmentRoot = query.from(Department.class);
        Root<UserCerti> userCertiRoot = query.from(UserCerti.class);
        Root<Certification> certificationRoot = query.from(Certification.class);

        // Select projection
        query.multiselect(
                userRoot.get("userId"),
                userRoot.get("fullname"),
                userRoot.get("birthdate"),
                departmentRoot.get("departmentName"),
                userRoot.get("email"),
                userRoot.get("telephone"),
                certificationRoot.get("certificationName"),
                userCertiRoot.get("endDate"),
                userCertiRoot.get("score")
        );

        // Where conditions
        List<Predicate> predicates = new ArrayList<>();

        // Join conditions
        predicates.add(cb.equal(userRoot.get("departmentId"), departmentRoot.get("departmentId")));
        predicates.add(cb.equal(userRoot.get("userId"), userCertiRoot.get("user").get("userId")));
        predicates.add(cb.equal(userCertiRoot.get("certification").get("certificationId"), certificationRoot.get("certificationId")));

        if (fullname != null && !fullname.isEmpty()) {
            predicates.add(cb.like(userRoot.get("fullname"), "%" + fullname + "%"));
        }

        if (departmentId != null && !departmentId.isEmpty()) {
            predicates.add(cb.equal(userRoot.get("departmentId"), departmentId));
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Order by
        List<Order> orders = new ArrayList<>();

        if ("DESC".equals(ordFullname)) {
            orders.add(cb.desc(userRoot.get("fullname")));
        } else if ("ASC".equals(ordFullname)) {
            orders.add(cb.asc(userRoot.get("fullname")));
        }

        if ("DESC".equals(ordCertificationName)) {
            orders.add(cb.desc(certificationRoot.get("certificationLevel")));
        } else if ("ASC".equals(ordCertificationName)) {
            orders.add(cb.asc(certificationRoot.get("certificationLevel")));
        }

        if ("DESC".equals(ordEndDate)) {
            orders.add(cb.desc(userCertiRoot.get("endDate")));
        } else if ("ASC".equals(ordEndDate)) {
            orders.add(cb.asc(userCertiRoot.get("endDate")));
        }

        // Default order by userId
        orders.add(cb.asc(userRoot.get("userId")));

        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }

        // Distinct to avoid duplicates
        query.distinct(true);

        TypedQuery<UserProjection> typedQuery = entityManager.createQuery(query);

        // Pagination
        if (offset != null) {
            typedQuery.setFirstResult(offset);
        }
        if (limit != null) {
            typedQuery.setMaxResults(limit);
        }

        return typedQuery.getResultList();
    }

    /**
     * Đếm số lượng users với criteria builder
     */
    public long countUsersWithCriteria(String fullname, String departmentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<User> userRoot = query.from(User.class);
        Root<Department> departmentRoot = query.from(Department.class);

        query.select(cb.countDistinct(userRoot.get("userId")));

        // Where conditions
        List<Predicate> predicates = new ArrayList<>();

        // Join condition
        predicates.add(cb.equal(userRoot.get("departmentId"), departmentRoot.get("departmentId")));

        if (fullname != null && !fullname.isEmpty()) {
            predicates.add(cb.like(userRoot.get("fullname"), "%" + fullname + "%"));
        }

        if (departmentId != null && !departmentId.isEmpty()) {
            predicates.add(cb.equal(userRoot.get("departmentId"), departmentId));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getSingleResult();
    }
} 