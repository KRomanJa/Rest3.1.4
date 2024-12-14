package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.MyUserDetails;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;

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
    public String admin(@AuthenticationPrincipal MyUserDetails myUserDetails, Model model) {
        model.addAttribute("admin", userService.findByUsername(myUserDetails.getUsername()));
        model.addAttribute("users", userService.allUsers());
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("showCreate", false);
        model.addAttribute("showEdit", false);
        return "admin";
    }

    @PostMapping("/edit")
    public String editAdmin(@AuthenticationPrincipal MyUserDetails myUserDetails, @Valid @ModelAttribute("user") User user,
                            BindingResult bindingResult, @RequestParam("id") Long id, Model model) {
        User currentUser = userService.findUserById(id);
        if (!currentUser.getUsername().equalsIgnoreCase(user.getUsername())) {
            if (userService.findByUsername(user.getUsername()) != null) {
                bindingResult.rejectValue("username", "error.user", "Логин уже используется");
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            model.addAttribute("users", userService.allUsers());
            model.addAttribute("admin", userService.findByUsername(myUserDetails.getUsername()));
            model.addAttribute("newUser", new User());
            model.addAttribute("allRoles", roleService.getAllRoles());
            model.addAttribute("showEdit", true);
            model.addAttribute("showCreate", false);
            return "admin";
        } else {
            model.addAttribute("showEdit", false);
            model.addAttribute("showCreate", false);
        }
        userService.updateUserById(id, user);
        return "redirect:/admin";
    }

    @PostMapping("/create")
    public String createAdmin(@AuthenticationPrincipal MyUserDetails myUserDetails, @Valid @ModelAttribute("newUser") User user,
                              BindingResult bindingResult, Model model) {
        if (userService.findByUsername(user.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.user", "Логин уже используется");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.allUsers());
            model.addAttribute("admin", userService.findByUsername(myUserDetails.getUsername()));
            model.addAttribute("allRoles", roleService.getAllRoles());
            model.addAttribute("showCreate", true);
            model.addAttribute("showEdit", false);
            return "admin";
        } else {
            model.addAttribute("showCreate", false);
            model.addAttribute("showEdit", false);
        }
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteAdmin(@RequestParam("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

}
