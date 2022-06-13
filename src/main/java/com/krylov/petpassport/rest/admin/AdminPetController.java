package com.krylov.petpassport.rest.admin;

import com.krylov.petpassport.model.Pet;
import com.krylov.petpassport.service.impl.PetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin/pets")
public class AdminPetController {
    private final PetServiceImpl petService;

    @Autowired
    public AdminPetController(PetServiceImpl petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets() {
        List<Pet> allPets = petService.findAllPets();
        return new ResponseEntity<>(allPets, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPet(@PathVariable("id") Long id) {
        Pet pet = petService.findPetById(id);
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
        Pet createdPet = petService.createPet(pet);
        return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Pet> updatePet(@RequestBody Pet pet) {
        Pet updatedPet = petService.updatePet(pet);
        return new ResponseEntity<>(updatedPet, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePet(@PathVariable("id") Long id) {
        petService.deletePetById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
