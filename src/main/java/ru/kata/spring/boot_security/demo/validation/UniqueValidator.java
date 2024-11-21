package ru.kata.spring.boot_security.demo.validation;

import org.springframework.beans.factory.annotation.Autowired;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class UniqueValidator implements ConstraintValidator<Unique, String> {

    @Autowired
    UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (userService != null) {
            List<User> users = userService.allUsers();
            for (User user1 : users) {
                if (user1.getUsername().equalsIgnoreCase(username)) {
                    return false;
                }
            }
        }
        return true;
    }
}
