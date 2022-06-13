package com.krylov.petpassport.service.impl;

import com.krylov.petpassport.exception.UserNotFoundException;
import com.krylov.petpassport.model.*;
import com.krylov.petpassport.repository.RoleRepository;
import com.krylov.petpassport.repository.UserRepository;
import com.krylov.petpassport.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.krylov.petpassport.config.SecurityConfig.passwordEncoder;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User addNewUser(User user) {
        Role roleUser = roleRepository.findByName(ERole.ROLE_USER);
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleUser);

        user.setPassword(passwordEncoder().encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Status.ACTIVE);
        user.setCreated(new Date());
        user.setUpdated(new Date());

        User registeredUser = userRepository.save(user);

        log.info("In register: user " + registeredUser.getEmail() + " successfully registered");

        return registeredUser;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> result = userRepository.findAll();

        log.info("In getAllUsers: " + result.size() + " users have been found ");

        return result;
    }

    @Override
    public User findUserById(Long userId) {
        User result = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User by id " + userId + " was not found"));

        log.info("In findByUserId: user " + result.getEmail() + " found by id: " + userId);

        return result;
    }

    @Override
    public User findByEmail(String email) {
        User result = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User by email " + email + " was not found"));

        log.info("In findByEmail: user " + email + " have been found");

        return result;
    }

    @Override
    public List<Pet> findPetsByUserId(Long userId) {
        User result = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User by id " + userId + " was not found"));

        log.info("In findPetsByUserId: Pets by user id " + userId + " have been found");

        return result.getPets();
    }

    @Override
    public User updateUser(User user) {

        log.info("In updateUser: user " + user.getEmail() + " successfully updated");

        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new UserNotFoundException("User with id " + userId + "does not exist");
        }

        userRepository.deleteById(userId);

        log.info("In deleteUserById: user with id " + userId + " successfully deleted");
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
