package com.svalero.cityEvents.repository;

import com.svalero.cityEvents.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();
    List<User> findUserByName(String name);
    List<User> findByBirthDateBefore(LocalDate date);
}
