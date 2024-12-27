package ru.kata.spring.boot_security.demo.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.MyUserDetails;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping
    public ResponseEntity<User> admin(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        User admin = userService.findByUsername(myUserDetails.getUsername());
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> allUsers() {
        return new ResponseEntity<>(userService.allUsers(), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (userService.findByUsername(user.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.user", "Логин уже используется");
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        userService.saveUser(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user, BindingResult bindingResult) {
        User currentUser = userService.findUserById(id);
        if (!currentUser.getUsername().equalsIgnoreCase(user.getUsername())) {
            if (userService.findByUsername(user.getUsername()) != null) {
                bindingResult.rejectValue("username", "error.user", "Логин уже используется");
            }
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        userService.updateUserById(id, user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> allRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

}
