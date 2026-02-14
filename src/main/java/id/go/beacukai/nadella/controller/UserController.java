package id.go.beacukai.nadella.controller;

import id.go.beacukai.nadella.entity.User;
import id.go.beacukai.nadella.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userService.findAll(pageable);

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/users/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        return "admin/users/form";
    }

    @PostMapping("/create")
    public String store(@ModelAttribute User user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username sudah digunakan!");
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        }

        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email sudah digunakan!");
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        }

        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "User berhasil ditambahkan!");
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return userService.findById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("roles", User.Role.values());
                    return "admin/users/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "User tidak ditemukan!");
                    return "redirect:/admin/users";
                });
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute User user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        return userService.findById(id)
                .map(existing -> {
                    user.setId(id);
                    user.setCreatedAt(existing.getCreatedAt());
                    userService.save(user);
                    redirectAttributes.addFlashAttribute("success", "User berhasil diupdate!");
                    return "redirect:/admin/users";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "User tidak ditemukan!");
                    return "redirect:/admin/users";
                });
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "User berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus user!");
        }
        return "redirect:/admin/users";
    }
}
