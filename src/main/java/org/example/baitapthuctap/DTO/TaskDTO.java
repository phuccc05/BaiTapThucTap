package org.example.baitapthuctap.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Integer id; // ID thường không cần validate vì do DB tự sinh (nếu là create)

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được dài quá 255 ký tự")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    private String moTa;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean trangThai;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    @NotBlank(message = "Username của người tạo không được để trống")
    @Size(max = 100, message = "Username không được dài quá 100 ký tự")
    private String usernameOwner;
}
