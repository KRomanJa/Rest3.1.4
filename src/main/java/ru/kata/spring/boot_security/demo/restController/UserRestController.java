package ru.kata.spring.boot_security.demo.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.MyUserDetails;
import ru.kata.spring.boot_security.demo.service.UserService;


@RestController
@RequestMapping("/api/user")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> user(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        User user = userService.findByUsername(myUserDetails.getUsername());
        return ResponseEntity.ok(user);
    }
}
