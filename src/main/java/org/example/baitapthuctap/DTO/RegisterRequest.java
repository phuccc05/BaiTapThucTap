package org.example.baitapthuctap.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private boolean gioiTinh;
    private String matKhau;
}
