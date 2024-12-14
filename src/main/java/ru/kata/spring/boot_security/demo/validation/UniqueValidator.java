package ru.kata.spring.boot_security.demo.validation;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.MyUserDetails;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.security.Principal;
import java.util.List;

public class UniqueValidator implements ConstraintValidator<Unique, String> {

    @Autowired
    private ApplicationContext context;

    private UserService userService;


    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (userService != null) {
            User user = userService.findByUsername(principal.getName());
            List<User> users = userService.allUsers();
            for (User user1 : users) {
                if (user1.getUsername().equalsIgnoreCase(username) && !user.getUsername().equals(username)) {
                    return false;
                }
            }
        }
        return true;
    }
}
