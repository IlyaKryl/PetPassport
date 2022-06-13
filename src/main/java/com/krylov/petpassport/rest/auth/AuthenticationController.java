package com.krylov.petpassport.rest.auth;

import com.krylov.petpassport.dto.request.LoginRequestDto;
import com.krylov.petpassport.dto.request.LogoutRequestDto;
import com.krylov.petpassport.dto.request.SignupRequestDto;
import com.krylov.petpassport.dto.request.TokenRefreshRequestDto;
import com.krylov.petpassport.dto.response.JwtResponseDto;
import com.krylov.petpassport.dto.response.MessageResponse;
import com.krylov.petpassport.dto.response.TokenRefreshResponseDto;
import com.krylov.petpassport.exception.TokenRefreshException;
import com.krylov.petpassport.model.RefreshToken;
import com.krylov.petpassport.model.User;
import com.krylov.petpassport.security.jwt.JwtTokenProvider;
import com.krylov.petpassport.security.jwt.JwtUser;
import com.krylov.petpassport.service.RefreshTokenService;
import com.krylov.petpassport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenProvider jwtTokenProvider,
                                    UserService userService,
                                    RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();

        String token = jwtTokenProvider.createToken(jwtUser);

        List<String> roles = jwtUser.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(jwtUser.getId());

        return ResponseEntity.ok(new JwtResponseDto(token, refreshToken.getToken(),
                jwtUser.getId(), jwtUser.getEmail(), roles));

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequestDto logoutRequestDto) {
        refreshTokenService.deleteByUserId(logoutRequestDto.getUserId());
        return ResponseEntity.ok(new MessageResponse("Log out successful"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequestDto tokenRefreshRequestDto) {
        String requestRefreshToken = tokenRefreshRequestDto.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtTokenProvider.createTokenFromEmail(user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponseDto(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        if (userService.existsByEmail(signupRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: email is already exist"));
        }

        User user = new User();
        user.setEmail(signupRequestDto.getEmail());
        user.setPassword(signupRequestDto.getPassword());
        user.setFirstName(signupRequestDto.getFirstName());
        user.setLastName(signupRequestDto.getLastName());

        userService.addNewUser(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }
}
