package com.training.backend.config;

/**
 * Class chứa các hằng số liên quan đến mã lỗi và thông báo
 */
public class MessageConstant {

    // Prevent instantiation
    private MessageConstant() {
    }

    // Error codes for API
    public static final String API_ERROR = "500";
    public static final String API_SUCCESS = "200";


    // Error codes
    public static final String ER001_CODE = "ER001";  // Không nhập
    public static final String ER002_CODE = "ER002";  // Không chọn
    public static final String ER003_CODE = "ER003";  // Check đã tồn tại
    public static final String ER004_CODE = "ER004";  // Check không tồn tại
    public static final String ER005_CODE = "ER005";  // Sai format
    public static final String ER006_CODE = "ER006";  // Check maxlength
    public static final String ER007_CODE = "ER007";  // Check độ dài trong khoảng
    public static final String ER008_CODE = "ER008";  // Check ký tự 1 byte
    public static final String ER009_CODE = "ER009";  // Check kana
    public static final String ER010_CODE = "ER010";  // Check hiragana
    public static final String ER011_CODE = "ER011";  // Ngày không hợp lệ
    public static final String ER012_CODE = "ER012";  // Ngày hết hạn < ngày cấp chứng chỉ
    public static final String ER013_CODE = "ER013";  // Biên tập user không tồn tại
    public static final String ER014_CODE = "ER014";  // User đích không tồn tại
    public static final String ER015_CODE = "ER015";  // Lỗi khi thao tác với database
    public static final String ER016_CODE = "ER016";  // Nhập sai Tên đăng nhập hoặc Mật khẩu
    public static final String ER017_CODE = "ER017";  // Mật khẩu xác nhận không đúng
    public static final String ER018_CODE = "ER018";  // Check phải là số halfsize
    public static final String ER019_CODE = "ER019";  // Tên đăng nhập không đúng định dạng
    public static final String ER020_CODE = "ER020";  // Kiểm tra user admin
    public static final String ER021_CODE = "ER021";  // Kiểm tra thứ tự sắp xếp
    public static final String ER022_CODE = "ER022";  // User đi chuyển đến trang không tồn tại
    public static final String ER023_CODE = "ER023";  // Lỗi hệ thống

    // Success message codes
    public static final String MSG001_CODE = "MSG001";  // Đăng ký User thành công
    public static final String MSG002_CODE = "MSG002";  // Update User thành công
    public static final String MSG003_CODE = "MSG003";  // Delete User thành công
    public static final String MSG004_CODE = "MSG004";  // Xác nhận trước khi xóa
    public static final String MSG005_CODE = "MSG005";  // Không tìm thấy user

    // Field names in Japanese
    public static final String FIELD_EMPLOYEE_ID = "ID";
    public static final String FIELD_ACCOUNT = "アカウント名";
    public static final String FIELD_NAME = "氏名";
    public static final String FIELD_KANA_NAME = "カタカナ氏名";
    public static final String FIELD_BIRTH_DATE = "生年月日";
    public static final String FIELD_EMAIL = "メールアドレス";
    public static final String FIELD_PHONE = "電話番号";
    public static final String FIELD_PASSWORD = "パスワード";
    public static final String FIELD_GROUP = "グループ";
    public static final String FIELD_CERTIFICATION = "資格";
    public static final String FIELD_CERTIFICATION_START_DATE = "資格交付日";
    public static final String FIELD_CERTIFICATION_END_DATE = "失効日";
    public static final String FIELD_SCORE = "点数";

    public static final String FIELD_OFFSET =  "オフセット";
    public static final String FIELD_LIMIT = "リミット";

    // Format patterns
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String ACCOUNT_FORMAT = "a-z, A-Z, 0-9 và _";
    public static final String PASSWORD_MIN_LENGTH = "8";
    public static final String PASSWORD_MAX_LENGTH = "50";

    // Success messages in Japanese
    public static final String MSG001_JP = "ユーザの登録が完了しました。";
    public static final String MSG002_JP = "ユーザの更新が完了しました。";
    public static final String MSG003_JP = "ユーザの削除が完了しました。";
    public static final String MSG004_JP = "削除するユーザが、よろしいでしょうか。";
    public static final String MSG005_JP = "検索条件に該当するユーザが見つかりません。";
}
