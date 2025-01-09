package org.limckmy.account.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.limckmy.account.dto.TokenDTO;
import org.limckmy.account.dto.UserDTO;
import org.limckmy.account.dto.UserLoginDTO;
import org.limckmy.account.dto.UserRegistrationDTO;
import org.limckmy.account.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@Slf4j
public class UserControllerV1 {

    private final UserService userService;

    public UserControllerV1(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserDTO userDTO =  userService.createUser(userRegistrationDTO);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> findUser(@PathVariable("username") String username) {
        UserDTO userDTO = userService.findByUsername(username);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO, @RequestHeader("User-Agent") String userAgent, HttpServletResponse response) {
        log.info("Login with : {} ", userAgent);
        TokenDTO tokenDTO = userService.login(userLoginDTO);

        if (isBrowser(userAgent)) {
            Cookie cookie = new Cookie("at", tokenDTO.accessToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // only send over TLS
            cookie.setMaxAge(3600); // Set expiration (1 hour)
        }

        return ResponseEntity.ok(tokenDTO);
    }

    private boolean isBrowser(String userAgent) {
        // Check for Edge
        if (userAgent.contains("Edg")) {
            return true; // It's Microsoft Edge
        }

        // Check for Chrome (excluding Edge)
        if (userAgent.contains("Chrome")) {
            return true; // It's Google Chrome
        }

        // Basic check for other browsers (Mozilla, Safari, etc.)
        return userAgent.contains("Mozilla") || userAgent.contains("Safari");
    }


}
