package com.svalero.cityEvents.controller;

import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.exception.ErrorResponse;
import com.svalero.cityEvents.exception.UserNotFoundException;
import com.svalero.cityEvents.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(value = "name", defaultValue = "") String name) throws UserNotFoundException {
        List<User> allUsers;

        if (!name.isEmpty()) {
            allUsers = userService.findUserByName(name);
        } else {
            allUsers = userService.findAll();
        }

        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/usurious/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) throws UserNotFoundException {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User newUser = userService.add(user);
        return new  ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<User> modifyUser(@PathVariable long id, @RequestBody User user) throws UserNotFoundException {
        User newUser = userService.modify(id, user);
        return ResponseEntity.ok(newUser);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) throws UserNotFoundException {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = new ErrorResponse(404,"not-found", "The user does not exist");
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }
}
