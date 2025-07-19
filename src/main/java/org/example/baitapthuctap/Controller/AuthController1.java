//package org.example.baitapthuctap.Controller;
//
//import org.example.baitapthuctap.Config.JwtUtil;
//import org.example.baitapthuctap.DTO.LoginRequest;
//import org.example.baitapthuctap.DTO.RegisterRequest;
//import org.example.baitapthuctap.Entity.Users;
//import org.example.baitapthuctap.Repository.UsersRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//    @Autowired
//    private UsersRepository usersRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
//        Users user = new Users();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
//        user.setNgayTao(LocalDateTime.now());
//        usersRepository.save(user);
//        return ResponseEntity.ok("Đăng ký thành công");
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getMatKhau())
//        );
//        Users user = usersRepository.findByUsername(request.getUsername());
//        String jwt = jwtUtil.generateToken(user);
//        return ResponseEntity.ok(Map.of("token", jwt));
//    }
//}
