package com.krylov.petpassport.rest.user;

import com.krylov.petpassport.dto.pet.PetFormDto;
import com.krylov.petpassport.model.Pet;
import com.krylov.petpassport.model.User;
import com.krylov.petpassport.security.jwt.JwtUser;
import com.krylov.petpassport.service.impl.PetServiceImpl;
import com.krylov.petpassport.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/pets")
public class PetController {
    private final PetServiceImpl petService;
    private final UserServiceImpl userService;

    @Autowired
    public PetController(PetServiceImpl petService, UserServiceImpl userService) {
        this.petService = petService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPet(@PathVariable("id") Long id) {
        Pet pet = petService.findPetById(id);
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Pet>> getUserPets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        User user = userService.findByEmail(jwtUser.getEmail());

        List<Pet> pets = userService.findPetsByUserId(user.getId());
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Pet> createPet(@Valid @RequestBody PetFormDto petDto) {
        Pet pet = new Pet();
        pet.setName(petDto.getName());
        pet.setPetType(petDto.getPetType());
        pet.setBreed(petDto.getBreed());
        pet.setSex(petDto.getSex());
        pet.setBirthDate(petDto.getBirthDate());
        pet.setFur(petDto.getFur());
        pet.setAdditionalInfo(petDto.getAdditionalInfo());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        User user = userService.findByEmail(jwtUser.getEmail());

        Pet createdPet = petService.createPet(pet);

        List<Pet> pets = user.getPets();
        pets.add(createdPet);
        user.setPets(pets);
        userService.updateUser(user);

        return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Pet> updatePet(@Valid @PathVariable("id") Long id, @RequestBody PetFormDto petDto) {
        Pet pet = petService.findPetById(id);
        pet.setName(petDto.getName());
        pet.setPetType(petDto.getPetType());
        pet.setBreed(petDto.getBreed());
        pet.setSex(petDto.getSex());
        pet.setBirthDate(petDto.getBirthDate());
        pet.setFur(petDto.getFur());
        pet.setAdditionalInfo(petDto.getAdditionalInfo());

        Pet updatedPet = petService.updatePet(pet);
        return new ResponseEntity<>(updatedPet, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deletePet(@PathVariable("id") Long id) {
        petService.deletePetById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/image/upload")
    public ResponseEntity<?> uploadImage(@PathVariable("id") Long id,
                                         @RequestParam("imageFile") MultipartFile imageFile) {

        try {
            petService.saveImageLinkToPetProfile(id, imageFile);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>((HttpStatus.BAD_REQUEST));
        }
    }

    @GetMapping("/{id}/image/download")
    public ResponseEntity<Resource> getImage(@PathVariable("id") Long id) {

        try {
            Resource resource = petService.downloadImageFromPetProfile(id);
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/image/delete")
    public ResponseEntity<?> deleteImage(@PathVariable("id") Long id) {

        try {
            petService.deleteImageFromPetProfile(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
