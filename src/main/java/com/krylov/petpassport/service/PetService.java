package com.krylov.petpassport.service;

import com.krylov.petpassport.model.Pet;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PetService {

    Pet createPet(Pet pet);

    List<Pet> findAllPets();

    Pet findPetById(Long petId);

    void deletePetById(Long petId);

    Pet updatePet(Pet pet);

    void saveImageLinkToPetProfile(Long id, MultipartFile imageFile) throws IOException;

    Resource downloadImageFromPetProfile(Long id) throws IOException;

    void deleteImageFromPetProfile(Long id) throws IOException;
}
