package com.krylov.petpassport.service;

import com.krylov.petpassport.model.Pet;
import com.krylov.petpassport.model.User;

import java.util.List;

public interface UserService {

    User addNewUser(User user);

    List<User> findAllUsers();

    User findUserById(Long id);

    User findByEmail(String email);

    List<Pet> findPetsByUserId(Long userId);

    void deleteUserById(Long id);

    User updateUser(User user);

    Boolean existsByEmail(String email);
}
