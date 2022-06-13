package com.krylov.petpassport.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pets")
@Data
public class Pet {

    @Id
    @SequenceGenerator(name = "pets_id_seq", sequenceName = "pets_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pets_id_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_type", nullable = false)
    private PetType petType;

    @Column(name = "breed")
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    private Sex sex;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "fur")
    private String fur;

    @Column(name = "additional_info")
    private String additionalInfo;

    @Column(name = "image_link")
    private String petProfileImageLink;
}
