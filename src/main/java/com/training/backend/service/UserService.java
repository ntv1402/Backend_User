package com.training.backend.service;

import com.training.backend.dto.UserDTO;
import com.training.backend.entity.User;
import com.training.backend.payload.request.FormRequest;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.payload.response.UserDetailResponse;

import java.util.List;

/**
 * Service interface cho User
 * Định nghĩa các phương thức quản lý user
 */
public interface UserService {

    /**
     * Lấy danh sách users với các điều kiện lọc và phân trang
     * 
     * @param userRequest Request object chứa các điều kiện tìm kiếm và phân trang
     * @return Danh sách UserDTO, null nếu có lỗi
     */
    List<UserDTO> listUsers(UserRequest userRequest);

    /**
     * Thêm mới một user vào hệ thống
     * 
     * @param formRequest Dữ liệu form chứa thông tin user và certifications
     * @return ID của user đã được tạo thành công, null nếu có lỗi
     */
    Long addUser(FormRequest formRequest);

    /**
     * Lấy thông tin chi tiết của một user theo ID
     * 
     * @param userId ID của user cần lấy thông tin
     * @return UserDetailResponse chứa thông tin chi tiết user
     * @throws Exception nếu không tìm thấy user
     */
    UserDetailResponse getUserById(Long userId) throws Exception;

    /**
     * Xóa user khỏi hệ thống
     * 
     * @param userId ID của user cần xóa
     * @return ID của user đã được xóa thành công
     */
    Long deleteUser(Long userId);

    /**
     * Cập nhật thông tin user hiện có
     * 
     * @param formRequest Dữ liệu form chứa thông tin cập nhật (bao gồm userId)
     * @return ID của user đã được cập nhật thành công, null nếu có lỗi
     */
    Long updateUser(FormRequest formRequest);

    /**
     * Đếm tổng số lượng users thỏa mãn điều kiện tìm kiếm
     * 
     * @param userRequest Request object chứa các điều kiện tìm kiếm
     * @return Tổng số lượng users thỏa mãn điều kiện
     */
    Long countUsers(UserRequest userRequest);
}
