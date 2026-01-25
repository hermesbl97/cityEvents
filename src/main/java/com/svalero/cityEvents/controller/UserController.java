package com.svalero.cityEvents.controller;

import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.dto.UserInDto;
import com.svalero.cityEvents.exception.ErrorResponse;
import com.svalero.cityEvents.exception.UserNotFoundException;
import com.svalero.cityEvents.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<User>> getAllUsers (
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "date", required = false) LocalDate date,
            @RequestParam(value = "active", required = false) Boolean active) {

        List<User> allUsers;

        if (name != null && !name.isEmpty()) {
            allUsers = userService.findUserByName(name);
        } else if (date != null){
            allUsers = userService.findUserBornBefore(date);
        } else if (active != null && active==false) {
            allUsers = userService.findUserNotActive();
        } else {
            allUsers = userService.findAll();
        }

        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) throws UserNotFoundException {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<User> addUser(@Valid @RequestBody UserInDto userInDto) {
        User newUser = userService.add(userInDto);
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
        ErrorResponse errorResponse = ErrorResponse.notFound("The user does not exist");
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        //extraemos los errores de la excepción del fallo
        manve.getBindingResult().getAllErrors().forEach(error -> { //para cada error rellenamos el nombre del campo
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName,message); //asociamos cada error con su mensaje
        });
        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.internalServerError();
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
