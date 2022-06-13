package com.krylov.petpassport.dto.pet;

import com.krylov.petpassport.model.PetType;
import com.krylov.petpassport.model.Sex;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter @Setter
public class PetFormDto {
    @Size(max = 50)
    private String name;

    @Size(max = 20)
    private PetType petType;

    @Size(max = 50)
    private String breed;

    @Size(max = 6)
    private Sex sex;

    @PastOrPresent
    private LocalDate birthDate;

    @Size(max = 50)
    private String fur;

    @Size(max = 250)
    private String additionalInfo;
}
