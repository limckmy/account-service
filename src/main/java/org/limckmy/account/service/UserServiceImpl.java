package org.limckmy.account.service;

import lombok.extern.slf4j.Slf4j;
import org.limckmy.account.dto.TokenDTO;
import org.limckmy.account.dto.UserDTO;
import org.limckmy.account.dto.UserLoginDTO;
import org.limckmy.account.dto.UserRegistrationDTO;
import org.limckmy.account.entity.User;
import org.limckmy.account.repository.UserRepository;
import org.limckmy.account.security.JWTUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDTO createUser(UserRegistrationDTO userRegistrationDTO) {
        log.debug("Attempting to create user");

        if (userRepository.existsByEmail(userRegistrationDTO.email())) {
            log.warn("Email already in use : {}", userRegistrationDTO.email());
            throw new IllegalArgumentException("Email already in use.");
        }

        if (userRepository.existsByUsername(userRegistrationDTO.username())) {
            log.warn("Username already in use : {}", userRegistrationDTO.username());
            throw new IllegalArgumentException("Username already in use.");
        }

        User user = new User()
                .setEmail(userRegistrationDTO.email())
                .setUsername(userRegistrationDTO.username())
                .setPassword(passwordEncoder.encode(userRegistrationDTO.password()));
        User userSaved = userRepository.save(user);
        return mapper(userSaved);
    }

    @Override
    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return mapper(user);
    }

    @Override
    public TokenDTO login(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByUsername(userLoginDTO.username()).orElseThrow(() -> new RuntimeException("Login failed"));
        if (passwordEncoder.matches(userLoginDTO.password(), user.getPassword())) {
            return new TokenDTO(jwtUtil.generateToken(user.getUsername()));
        }
        throw new RuntimeException("Login failed");
    }

    private UserDTO mapper(User user) {
        return new UserDTO(user.getUsername(), user.getEmail(), user.getId());
    }
}
