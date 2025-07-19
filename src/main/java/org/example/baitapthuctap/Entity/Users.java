package org.example.baitapthuctap.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @Column(name = "ngay_tao")
    @DateTimeFormat(pattern = "dd/M/yyyy HH:mm:ss")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    @DateTimeFormat(pattern = "dd/M/yyyy HH:mm:ss")
    private LocalDateTime ngaySua;
}