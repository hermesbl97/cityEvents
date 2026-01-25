package com.svalero.cityEvents.service;

import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.dto.UserInDto;
import com.svalero.cityEvents.exception.UserNotFoundException;
import com.svalero.cityEvents.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public User add(UserInDto userInDto) {
        User user = new User();
        modelMapper.map(userInDto,user);
        return userRepository.save(user);
    }

    public void delete(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
    }

    public List<User> findAll() {
        List<User> allUsers = userRepository.findAll();
        return allUsers;
    }

    public User findUserById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return user;
    }

    public List<User> findUserByName(String name) {
        List<User> users = userRepository.findUserByName(name);
        return users;
    }

    public List<User> findUserBornBefore(LocalDate date) {
        return userRepository.findByBirthDateBefore(date);
    }

    public List<User> findUserNotActive() {
        return userRepository.findByActiveFalse();
    }

    public User modify(long id, User user) throws UserNotFoundException {
        User userExisting = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        modelMapper.map(user, userExisting);
        userExisting.setId(id);

        return userRepository.save(userExisting);
    }
}
