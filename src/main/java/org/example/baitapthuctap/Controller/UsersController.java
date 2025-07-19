package org.example.baitapthuctap.Controller;

import org.example.baitapthuctap.Entity.Users;
import org.example.baitapthuctap.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UsersController {
    @Autowired
    UsersRepository usersRepository;

    @GetMapping("/user/home")
    public String hienThi(Model model) {
        List<Users> listU = usersRepository.findAll();

        model.addAttribute("listU", listU);
        return "/Users";
    }

    @PostMapping("/user/add")
    public String addUser(@ModelAttribute Users users) {
        if (users.getNgayTao() == null) {
            users.setNgayTao(LocalDateTime.now());
        }
        usersRepository.save(users);
        return "redirect:/user/home";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        usersRepository.deleteById(id);
        return "redirect:/user/home";
    }

    @GetMapping("/user/detail/{id}")
    public String editUser(@PathVariable("id") Integer id, Model model) {
        Users user = usersRepository.findById(id).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            return "/Detail";
        }
        return "redirect:/user/home";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute Users users) {
        Users existingUser = usersRepository.findById(users.getId()).orElse(null);

        if (existingUser != null) {
            existingUser.setUsername(users.getUsername());
            existingUser.setEmail(users.getEmail());
            existingUser.setMatKhau(users.getMatKhau());
            existingUser.setGioiTinh(users.getGioiTinh());
            // Giữ nguyên ngày tạo
            existingUser.setNgaySua(LocalDateTime.now());

            usersRepository.save(existingUser);
        }

        return "redirect:/user/home";
    }



}
