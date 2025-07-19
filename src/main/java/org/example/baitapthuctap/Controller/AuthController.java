package org.example.baitapthuctap.Controller;

import org.example.baitapthuctap.Config.JwtUtil;
import org.example.baitapthuctap.DTO.LoginRequest;
import org.example.baitapthuctap.DTO.RegisterRequest;
import org.example.baitapthuctap.Entity.Users;
import org.example.baitapthuctap.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    //POST /api/auth/register - Đăng ký
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Kiểm tra username
            if (usersRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Username đã tồn tại"));
            }
            // Kiểm tra email
            if (usersRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Email đã tồn tại"));
            }
            // Tạo user mới
            Users user = new Users();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
            user.setGioiTinh(request.isGioiTinh() ? "Nam" : "Nữ");
            user.setNgayTao(LocalDateTime.now());

            Users savedUser = usersRepository.save(user);
            // Tạo JWT
            String token = jwtUtil.generateToken(savedUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Đăng ký thành công",
                    "token", token,
                    "user", Map.of(
                            "id", savedUser.getId(),
                            "username", savedUser.getUsername(),
                            "email", savedUser.getEmail(),
                            "gioiTinh", savedUser.getGioiTinh()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi đăng ký"));
        }
    }

    //POST /api/auth/login - Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Xác thực thông tin đăng nhập
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getMatKhau())
            );
            // Lấy thông tin user
            Users user = usersRepository.findByUsername(request.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Thông tin đăng nhập không chính xác"));
            }

            // Tạo JWT token
            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập thành công",
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "gioiTinh", user.getGioiTinh()
                    )
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Thông tin đăng nhập không chính xác"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi đăng nhập"));
        }
    }

    //GET /api/auth/profile - Lấy thông tin profile user
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // Lấy username từ token
            String jwt = token.substring(7); // Bỏ "Bearer "
            String username = jwtUtil.extractUsername(jwt);

            // Lấy thông tin user
            Users user = usersRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy user"));
            }
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "gioiTinh", user.getGioiTinh(),
                    "ngayTao", user.getNgayTao(),
                    "ngaySua", user.getNgaySua()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy thông tin profile"));
        }
    }

    //POST /api/auth/logout - Đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of(
                "message", "Đăng xuất thành công"
        ));
    }
}