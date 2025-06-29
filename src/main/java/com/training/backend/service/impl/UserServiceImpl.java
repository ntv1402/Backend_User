package com.training.backend.service.impl;

import com.training.backend.payload.response.UserDetailResponse.CertificationDetail;
import com.training.backend.dto.UserDTO;
import com.training.backend.payload.response.UserDetailResponse;
import com.training.backend.dto.UserProjection;
import com.training.backend.entity.Certification;
import com.training.backend.entity.Department;
import com.training.backend.entity.User;
import com.training.backend.entity.UserCerti;
import com.training.backend.exception.NotFoundException;
import com.training.backend.payload.response.ListResponse;
import com.training.backend.payload.request.CertificationRequest;
import com.training.backend.payload.request.FormRequest;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.repository.CertificationRepository;
import com.training.backend.repository.DepartmentRepository;
import com.training.backend.repository.UserCertiRepository;
import com.training.backend.repository.UserRepository;
import com.training.backend.repository.UserRepositoryImpl;
import com.training.backend.service.UserService;
import com.training.backend.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation class cho UserService
 * Cung cấp các chức năng quản lý user bao gồm CRUD operations và tìm kiếm
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private UserCertiRepository userCertiRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    /**
     * Thêm mới một user vào hệ thống
     * Bao gồm việc tạo user và các certifications liên quan
     * 
     * @param formRequest Dữ liệu form chứa thông tin user và certifications
     * @return User đã được tạo thành công, null nếu có lỗi
     */
    @Override
    @Transactional
    public Long addUser(FormRequest formRequest) {
        try {
            log.info("Bắt đầu thêm mới user với username: {}", formRequest.getUsername());

            // Kiểm tra username đã tồn tại chưa
            if (userRepository.existsByUsername(formRequest.getUsername())) {
                log.warn("Username '{}' đã tồn tại trong hệ thống", formRequest.getUsername());
                return null;
            }

            // Kiểm tra email đã tồn tại chưa
            if (userRepository.existsByEmail(formRequest.getEmail())) {
                log.warn("Email '{}' đã tồn tại trong hệ thống", formRequest.getEmail());
                return null;
            }

            // Tạo đối tượng User mới
            User user = new User();
            
            // Thiết lập thông tin cơ bản cho user
            setUserData(user, formRequest);
            
            // Mã hóa password trước khi lưu
            setUserPassword(user, formRequest.getPassword());

            // Lưu user vào database
            User savedUser = userRepository.save(user);
            log.info("Đã lưu user thành công với ID: {}", savedUser.getUserId());

            // Xử lý các certifications nếu có
            if (formRequest.getCertifications() != null && !formRequest.getCertifications().isEmpty()) {
                for (CertificationRequest certRequest : formRequest.getCertifications()) {
                    // Tìm certification trong database
                    Certification certification = certificationRepository.findById(certRequest.getCertificationId())
                            .orElse(null);
                    
                    if (certification != null) {
                        // Tạo UserCerti và thiết lập thông tin
                        UserCerti userCerti = setUserCertiData(certRequest, savedUser, certification);
                        userCertiRepository.save(userCerti);
                        log.debug("Đã lưu certification '{}' cho user ID: {}", 
                                certification.getCertificationName(), savedUser.getUserId());
                    } else {
                        log.warn("Không tìm thấy certification với ID: {}", certRequest.getCertificationId());
                    }
                }
            }

            log.info("Hoàn thành thêm mới user với ID: {}", savedUser.getUserId());
            return savedUser.getUserId();

        } catch (Exception e) {
            log.error("Lỗi khi thêm mới user với username: {}. Chi tiết lỗi: {}", 
                    formRequest.getUsername(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Cập nhật thông tin user hiện có
     * Bao gồm việc cập nhật thông tin user và certifications
     * 
     * @param formRequest Dữ liệu form chứa thông tin cập nhật
     * @return User đã được cập nhật thành công, null nếu có lỗi
     */
    @Override
    @Transactional
    public Long updateUser(FormRequest formRequest) {
        Long userId = formRequest.getUserId();
        try {
            log.info("Bắt đầu cập nhật user với ID: {}", userId);

            // Tìm user hiện có trong database
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy user với ID: " + userId));

            // Kiểm tra email có bị trùng với user khác không
            if (userRepository.existsByEmailAndUserIdNot(formRequest.getEmail(), userId)) {
                log.warn("Email '{}' đã được sử dụng bởi user khác", formRequest.getEmail());
                return null;
            }

            // Cập nhật thông tin cơ bản cho user
            setUserData(existingUser, formRequest);
            
            // Cập nhật password nếu có thay đổi
            if (formRequest.getPassword() != null && !formRequest.getPassword().isEmpty()) {
                setUserPassword(existingUser, formRequest.getPassword());
            }

            // Lưu user đã cập nhật
            User updatedUser = userRepository.save(existingUser);
            log.info("Đã cập nhật thông tin user thành công với ID: {}", updatedUser.getUserId());

            // Xóa tất cả certifications cũ của user
            userCertiRepository.deleteByUserId(userId);
            log.debug("Đã xóa tất cả certifications cũ của user ID: {}", userId);

            // Thêm lại các certifications mới
            if (formRequest.getCertifications() != null && !formRequest.getCertifications().isEmpty()) {
                for (CertificationRequest certRequest : formRequest.getCertifications()) {
                    // Tìm certification trong database
                    Certification certification = certificationRepository.findById(certRequest.getCertificationId())
                            .orElse(null);
                    
                    if (certification != null) {
                        // Tạo UserCerti mới và thiết lập thông tin
                        UserCerti userCerti = setUserCertiData(certRequest, updatedUser, certification);
                        userCertiRepository.save(userCerti);
                        log.debug("Đã cập nhật certification '{}' cho user ID: {}", 
                                certification.getCertificationName(), updatedUser.getUserId());
                    } else {
                        log.warn("Không tìm thấy certification với ID: {}", certRequest.getCertificationId());
                    }
                }
            }

            log.info("Hoàn thành cập nhật user với ID: {}", updatedUser.getUserId());
            return updatedUser.getUserId();

        } catch (NotFoundException e) {
            log.error("Không tìm thấy user với ID: {}. Chi tiết lỗi: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật user với ID: {}. Chi tiết lỗi: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Lấy danh sách users với các điều kiện lọc và phân trang
     * Sử dụng Criteria Builder thay vì SQL queries truyền thống
     * 
     * @param userRequest Request object chứa các điều kiện tìm kiếm và phân trang
     * @return Danh sách UserDTO, null nếu có lỗi
     */
    @Override
    public List<UserDTO> listUsers(UserRequest userRequest) {
        try {
            log.info("Bắt đầu lấy danh sách users với các tham số: fullname={}, departmentId={}, offset={}, limit={}", 
                    userRequest.getFullname(), userRequest.getDepartmentId(), userRequest.getOffset(), userRequest.getLimit());

            // Lấy danh sách users từ repository sử dụng Criteria Builder
            List<UserProjection> projections = userRepositoryImpl.searchUsersWithCriteria(
                    userRequest.getFullname(), 
                    userRequest.getDepartmentId(), 
                    userRequest.getOrdFullname(), 
                    userRequest.getOrdCertificationName(), 
                    userRequest.getOrdEndDate(), 
                    userRequest.getOffset(), 
                    userRequest.getLimit()
            );

            // Chuyển đổi projections thành DTOs
            List<UserDTO> userDTOs = projections.stream()
                    .map(this::mapUserProjectionToUserDTO)
                    .collect(Collectors.toList());

            log.info("Hoàn thành lấy danh sách users. Số records trả về: {}", userDTOs.size());

            return userDTOs;

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách users. Chi tiết lỗi: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Lấy thông tin chi tiết của một user theo ID
     * Bao gồm thông tin user và danh sách certifications
     * 
     * @param userId ID của user cần lấy thông tin
     * @return UserDetailResponse chứa thông tin chi tiết user, null nếu không tìm thấy
     */
    @Override
    public UserDetailResponse getUserById(Long userId) {
        try {
            log.info("Bắt đầu lấy thông tin chi tiết user với ID: {}", userId);

            // Tìm user trong database
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy user với ID: " + userId));

            // Tìm department của user
            Department department = departmentRepository.findById(user.getDepartmentId())
                    .orElse(null);

            // Lấy danh sách certifications của user
            List<UserCerti> userCertis = userCertiRepository.findByUserId(userId);

            // Chuyển đổi UserCerti thành CertificationDetail
            List<CertificationDetail> certificationDetails = userCertis.stream()
                    .map(this::mapUserCertiToCertificationDetail)
                    .collect(Collectors.toList());

            // Tạo response object
            UserDetailResponse response = new UserDetailResponse();
            setUserDetailResponseFields(response, user, department, certificationDetails);

            log.info("Hoàn thành lấy thông tin chi tiết user với ID: {}", userId);
            return response;

        } catch (NotFoundException e) {
            log.error("Không tìm thấy user với ID: {}. Chi tiết lỗi: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin user với ID: {}. Chi tiết lỗi: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Thiết lập thông tin cơ bản cho user từ FormRequest
     * Helper method để tái sử dụng logic thiết lập user data
     * 
     * @param user Đối tượng User cần thiết lập
     * @param formRequest Dữ liệu form chứa thông tin user
     */
    private void setUserData(User user, FormRequest formRequest) {
        user.setFullname(formRequest.getFullname());
        user.setBirthdate(DateUtils.parseDate(formRequest.getBirthDate()));
        user.setKatakana(formRequest.getKatakana());
        user.setDepartmentId(formRequest.getDepartmentId());
        user.setEmail(formRequest.getEmail());
        user.setTelephone(formRequest.getTelephone());
        user.setUsername(formRequest.getUsername());
    }

    /**
     * Thiết lập password đã mã hóa cho user
     * Helper method để tái sử dụng logic mã hóa password
     * 
     * @param user Đối tượng User cần thiết lập password
     * @param password Password chưa mã hóa
     */
    private void setUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }

    /**
     * Tạo và thiết lập thông tin cho UserCerti từ CertificationRequest
     * Sử dụng Builder pattern để tạo object
     * 
     * @param certRequest Dữ liệu certification request
     * @param user Đối tượng User
     * @param certification Đối tượng Certification
     * @return UserCerti đã được thiết lập đầy đủ
     */
    private UserCerti setUserCertiData(CertificationRequest certRequest, User user, Certification certification) {
        return UserCerti.builder()
                .user(user)
                .certification(certification)
                .startDate(DateUtils.parseDate(certRequest.getCertificationStartDate()))
                .endDate(DateUtils.parseDate(certRequest.getCertificationEndDate()))
                .score(certRequest.getUserCertificationScore())
                .build();
    }

    /**
     * Chuyển đổi UserProjection thành UserDTO
     * Helper method để mapping dữ liệu
     * 
     * @param projection UserProjection từ database
     * @return UserDTO đã được chuyển đổi
     */
    private UserDTO mapUserProjectionToUserDTO(UserProjection projection) {
        UserDTO dto = new UserDTO();
        dto.setUserId(projection.getUserId());
        dto.setFullname(projection.getFullname());
        dto.setBirthdate(projection.getBirthDate());
        dto.setDepartmentName(projection.getDepartmentName());
        dto.setEmail(projection.getEmail());
        dto.setTelephone(projection.getTelephone());
        dto.setCertificationName(projection.getCertificationName());
        dto.setEndDate(projection.getEndDate());
        dto.setScore(projection.getScore());
        return dto;
    }

    /**
     * Chuyển đổi UserCerti thành CertificationDetail
     * Helper method để mapping dữ liệu certification
     * 
     * @param userCerti UserCerti từ database
     * @return CertificationDetail đã được chuyển đổi
     */
    private CertificationDetail mapUserCertiToCertificationDetail(UserCerti userCerti) {
        CertificationDetail detail = new CertificationDetail();
        detail.setCertificationId(userCerti.getCertification().getCertificationId());
        detail.setCertificationName(userCerti.getCertification().getCertificationName());
        detail.setStartDate(userCerti.getStartDate());
        detail.setEndDate(userCerti.getEndDate());
        detail.setScore(userCerti.getScore());
        return detail;
    }

    /**
     * Thiết lập các trường cho UserDetailResponse
     * Helper method để tái sử dụng logic thiết lập response
     * 
     * @param response Đối tượng UserDetailResponse cần thiết lập
     * @param user Đối tượng User
     * @param department Đối tượng Department (có thể null)
     * @param certificationDetails Danh sách CertificationDetail
     */
    private void setUserDetailResponseFields(UserDetailResponse response, User user, Department department, 
                                           List<CertificationDetail> certificationDetails) {
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullname());
        response.setBirthDate(user.getBirthdate());
        response.setDepartmentId(user.getDepartmentId());
        response.setDepartmentName(department != null ? department.getDepartmentName() : null);
        response.setEmail(user.getEmail());
        response.setTelephone(user.getTelephone());
        response.setUsername(user.getUsername());
        response.setCertifications(certificationDetails);
    }

    /**
     * Xóa user khỏi hệ thống
     * Bao gồm việc xóa tất cả certifications liên quan trước khi xóa user
     * 
     * @param userId ID của user cần xóa
     * @return ID của user đã được xóa thành công
     * @throws RuntimeException nếu có lỗi xảy ra trong quá trình xóa
     */
    @Override
    @Transactional
    public Long deleteUser(Long userId) {
        try {
            log.info("Bắt đầu xóa user với ID: {}", userId);
            
            // Xóa tất cả certifications của user trước
            userCertiRepository.deleteByUserId(userId);
            log.debug("Đã xóa tất cả certifications của user ID: {}", userId);
            
            // Xóa user
            userRepository.deleteById(userId);
            log.info("Hoàn thành xóa user với ID: {}", userId);
            
            return userId;
        } catch (Exception e) {
            log.error("Lỗi khi xóa user với ID: {}. Chi tiết lỗi: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Không thể xóa user với ID: " + userId, e);
        }
    }

    /**
     * Đếm tổng số lượng users thỏa mãn điều kiện tìm kiếm
     * Sử dụng Criteria Builder để đếm với các điều kiện lọc
     * 
     * @param userRequest Request object chứa các điều kiện tìm kiếm
     * @return Tổng số lượng users thỏa mãn điều kiện
     */
    @Override
    public Long countUsers(UserRequest userRequest) {
        try {
            log.debug("Đếm số lượng users với điều kiện: fullname={}, departmentId={}", 
                    userRequest.getFullname(), userRequest.getDepartmentId());
            
            long count = userRepositoryImpl.countUsersWithCriteria(
                    userRequest.getFullname(),
                    userRequest.getDepartmentId()
            );
            
            log.debug("Kết quả đếm: {} users", count);
            return count;
        } catch (Exception e) {
            log.error("Lỗi khi đếm số lượng users. Chi tiết lỗi: {}", e.getMessage(), e);
            return 0L;
        }
    }
}
