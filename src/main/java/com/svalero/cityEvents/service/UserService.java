package com.svalero.cityEvents.service;

import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.dto.ReviewOutDto;
import com.svalero.cityEvents.dto.UserInDto;
import com.svalero.cityEvents.dto.UserOutDto;
import com.svalero.cityEvents.exception.UserNotFoundException;
import com.svalero.cityEvents.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    public List<UserOutDto> findAll(String name, LocalDate date, Boolean active) {

        List<User> allUsers;

        if (name != null && !name.isEmpty()) {
            allUsers = userRepository.findUserByName(name);
        } else if (date != null){
            allUsers = userRepository.findByBirthDateBefore(date);
        } else if (active != null && active==true) {
            allUsers = userRepository.findByActiveFalse();
        } else {
            allUsers = userRepository.findAll();
        }

        List<UserOutDto> usersOutDto = modelMapper.map(allUsers, new TypeToken<List<UserOutDto>>() {}.getType());

        return usersOutDto;
    }

    public User findUserById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return user;
    }

    public User modify(long id, User user) throws UserNotFoundException {
        User userExisting = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        modelMapper.map(user, userExisting);
        userExisting.setId(id);

        return userRepository.save(userExisting);
    }
}
