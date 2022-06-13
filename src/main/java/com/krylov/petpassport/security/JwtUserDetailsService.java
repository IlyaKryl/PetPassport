package com.krylov.petpassport.security;

import com.krylov.petpassport.model.User;
import com.krylov.petpassport.security.jwt.JwtUser;
import com.krylov.petpassport.security.jwt.JwtUserFactory;
import com.krylov.petpassport.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);

        JwtUser jwtUser = JwtUserFactory.create(user);

        log.info("In loadUserByUsername: user with email " + email + " successfully loaded");

        return jwtUser;
    }
}
