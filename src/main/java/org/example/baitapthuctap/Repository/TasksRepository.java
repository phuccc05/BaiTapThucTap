package org.example.baitapthuctap.Repository;

import org.example.baitapthuctap.Entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Integer> {

    Page<Tasks> findByUsersId(Integer userId, Pageable pageable);
    // Tìm tất cả tasks của một user
    List<Tasks> findByUsersId(Integer userId);

    // Tìm task theo ID và user ID (để đảm bảo chỉ owner mới truy cập được)
    Optional<Tasks> findByIdAndUsersId(Integer id, Integer userId);

    // Tìm tasks theo trạng thái và user
    List<Tasks> findByUsersIdAndTrangThai(Integer userId, Boolean trangThai);

    // Tìm tasks theo title và user (search)
    @Query("SELECT t FROM Tasks t WHERE t.users.id = :userId AND t.title LIKE %:title%")
    List<Tasks> findByUsersIdAndTitleContaining(@Param("userId") Integer userId, @Param("title") String title);

    // Đếm số lượng tasks hoàn thành của user
    @Query("SELECT COUNT(t) FROM Tasks t WHERE t.users.id = :userId AND t.trangThai = true")
    Long countCompletedTasksByUser(@Param("userId") Integer userId);

    // Đếm số lượng tasks chưa hoàn thành của user
    @Query("SELECT COUNT(t) FROM Tasks t WHERE t.users.id = :userId AND t.trangThai = false")
    Long countPendingTasksByUser(@Param("userId") Integer userId);

    List<Tasks> findByTrangThai(Boolean trangThai);

    List<Tasks> findAll();
    Optional<Tasks> findById(Integer id);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    Long countByTrangThai(Boolean trangThai);

    List<Tasks> findByTitleContainingIgnoreCase(String title);
}
