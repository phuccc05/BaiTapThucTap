package org.example.baitapthuctap.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
    private Integer id;
    private String username;
    private String email;
    private String gioiTinh;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Double completionRate;
    private boolean isAdmin;
}