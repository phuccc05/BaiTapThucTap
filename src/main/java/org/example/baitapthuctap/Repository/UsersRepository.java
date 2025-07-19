package org.example.baitapthuctap.Repository;

import org.example.baitapthuctap.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    // Tìm user theo username
    Users findByUsername(String username);

    // Tìm user theo email
    Optional<Users> findByEmail(String email);

    // Kiểm tra username có tồn tại không
    boolean existsByUsername(String username);

    // Kiểm tra email có tồn tại không
    boolean existsByEmail(String email);


}
