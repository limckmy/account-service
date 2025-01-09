package org.limckmy.account.service;

import org.limckmy.account.dto.TokenDTO;
import org.limckmy.account.dto.UserDTO;
import org.limckmy.account.dto.UserLoginDTO;
import org.limckmy.account.dto.UserRegistrationDTO;

import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserRegistrationDTO userRegistrationDTO);
    UserDTO findByUsername(String username);
    TokenDTO login(UserLoginDTO userLoginDTO);

}
