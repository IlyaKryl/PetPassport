package com.krylov.petpassport.rest.user;

import com.krylov.petpassport.model.User;
import com.krylov.petpassport.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    public final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

//    @GetMapping("/{id}/pets")
//    public ResponseEntity<List<Pet>> getUserPets(@PathVariable("id") Long id) {
//        List<Pet> pets = userService.findPetsByUserId(id);
//        return new ResponseEntity<>(pets, HttpStatus.OK);
//    }

//    @PostMapping("/add")
//    public ResponseEntity<User> registerUser(@RequestBody User user) {
//        User registeredUser = userService.addNewUser(user);
//        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
//    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
