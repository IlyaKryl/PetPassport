package com.krylov.petpassport.service.impl;

import com.krylov.petpassport.exception.PetNotFoundException;
import com.krylov.petpassport.model.Pet;
import com.krylov.petpassport.repository.PetRepository;
import com.krylov.petpassport.service.PetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    @Value("${photos.folder}")
    private String folder;

    @Autowired
    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Pet createPet(Pet pet) {
        Pet result = petRepository.save(pet);

        log.info("In createPet: " + result + " has been created");

        return result;
    }

    @Override
    public List<Pet> findAllPets() {
        List<Pet> result = petRepository.findAll();

        log.info("In getAllPets: " + result.size() + " pets have been found ");

        return result;
    }

    @Override
    public Pet findPetById(Long petId) {
        Pet result = petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Pet by id " + petId + " was not found"));

        log.info("In findPetById: pet " + result.getName() + " found by id: " + petId);

        return result;
    }

    @Override
    public Pet updatePet(Pet pet) {
        log.info("In updatePet: pet " + pet.getName() + "successfully updated");

        return petRepository.save(pet);
    }

    @Override
    public void deletePetById(Long petId) {
        boolean exists = petRepository.existsById(petId);

        if (!exists) {
            throw new PetNotFoundException("Pet with id " + petId + "does not exist");
        }

        petRepository.deleteById(petId);

        log.info("In deletePetById: pet with id " + petId + " successfully deleted");
    }

    @Transactional(rollbackFor = {IOException.class})
    @Override
    public void saveImageLinkToPetProfile(Long id, MultipartFile imageFile) throws IOException {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet by id " + id + " was not found"));

        if (pet.getPetProfileImageLink() != null) {
            deleteImageFromPetProfile(id);
        }

        byte[] bytes = imageFile.getBytes();
        Path path = Paths.get(folder + imageFile.getOriginalFilename());
        Files.write(path, bytes);

        pet.setPetProfileImageLink(path.toString());

        log.info("In saveImageLinkToPetProfile: " + imageFile + " successfully uploaded for pet with " + id + " id");

        petRepository.save(pet);
    }

    @Override
    public Resource downloadImageFromPetProfile(Long id) throws IOException {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet by id " + id + " was not found"));

        String imageLink = pet.getPetProfileImageLink();
        Path path = Paths.get(imageLink);
        byte[] bytes = Files.readAllBytes(path);

        Resource resource = new ByteArrayResource(bytes);
        if (resource.exists() || resource.isReadable()) {
            log.info("In downloadImageFromPetProfile: image successfully downloaded for pet with " + id + " id");
            return resource;
        } else {
            throw new IOException();
        }
    }

    @Transactional(rollbackFor = {IOException.class})
    @Override
    public void deleteImageFromPetProfile(Long id) throws IOException {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet by id " + id + " was not found"));

        String imageLink = pet.getPetProfileImageLink();
        Path path = Paths.get(imageLink);
        Files.deleteIfExists(path);

        pet.setPetProfileImageLink(null);

        log.info("In deleteImageFromPetProfile: image successfully deleted for pet with " + id + " id");

        petRepository.save(pet);
    }
}
