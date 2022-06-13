package com.krylov.petpassport.repository;

import com.krylov.petpassport.model.ERole;
import com.krylov.petpassport.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(ERole name);
}
