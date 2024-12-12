package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String admin(Principal principal, Model model) {
        model.addAttribute("users", userService.allUsers());
        model.addAttribute("admin", userService.findByUsername(principal.getName()));
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin";
    }

    @PostMapping("/edit")
    public String editAdmin(@ModelAttribute("user") User user, @RequestParam("id") Long id) {
        userService.updateUserById(id, user);
        return "redirect:/admin";
    }

    @PostMapping("/create")
    public String createAdmin(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteAdmin(@RequestParam("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

}
