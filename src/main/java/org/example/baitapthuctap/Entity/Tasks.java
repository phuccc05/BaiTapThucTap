package org.example.baitapthuctap.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Tasks {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private Users users;

    @Column(name = "ngay_tao")
    @DateTimeFormat(pattern = "dd/M/yyyy HH:mm:ss")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    @DateTimeFormat(pattern = "dd/M/yyyy HH:mm:ss")
    private LocalDateTime ngaySua;

}
