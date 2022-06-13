package com.krylov.petpassport.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refreshtokens")
@Getter
@Setter
public class RefreshToken {

    @Id
    @SequenceGenerator(name = "refreshtokens_id_seq", sequenceName = "refreshtokens_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refreshtokens_id_seq")
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
