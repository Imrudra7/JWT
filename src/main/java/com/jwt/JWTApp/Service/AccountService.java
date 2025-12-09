package com.jwt.JWTApp.Service;

import com.jwt.JWTApp.DTO.AddUserRequestDTO;
import com.jwt.JWTApp.DTO.LoginRequestDTO;
import com.jwt.JWTApp.DTO.LoginResponseDTO;
import com.jwt.JWTApp.DTO.UserDTO;
import com.jwt.JWTApp.Globals.JWTUtil;
import com.jwt.JWTApp.Model.User;
import com.jwt.JWTApp.Repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwt;
    private final AuthenticationManager authManager;


    //@CachePut(cacheNames = "users", key = "#p0.userEmail")
    public UserDTO addUser(AddUserRequestDTO addUserRequestDTO) {
        log.debug("Inside addUser of Account Service {},",addUserRequestDTO.toString());

        Optional<User> optional =  userRepo.findByUserEmail(addUserRequestDTO.getUserEmail());
        if (optional.isPresent()) return null;
        User newUser = modelMapper.map(addUserRequestDTO, User.class);
        newUser.setPassword(passwordEncoder.encode(addUserRequestDTO.getPassword()));

        User saved = userRepo.save(newUser);
        UserDTO savUserDTO = modelMapper.map(saved, UserDTO.class);
        return savUserDTO;

    }

    @Cacheable(cacheNames = "users", key = "#userDTO.userEmail")
    public UserDTO getUser(UserDTO userDTO) {
        log.debug("Inside getUser of Account Service {}", userDTO.toString());
        User user = modelMapper.map(userDTO, User.class);
        Optional<User> optional =  userRepo.findByUserEmail(user.getUserEmail());
        if (optional.isEmpty()) return null;

       User found = optional.get();
        UserDTO savUserDTO = modelMapper.map(found, UserDTO.class);
        return savUserDTO;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        log.debug("Inside login method of AccountSerVice with input:{}",loginRequestDTO.toString());
        try {
            log.debug("Calling the authenticate.");
            // yahan se .CustomUserDetailsService ka loadUserByUsername call hoga
            Authentication authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUserEmail(),loginRequestDTO.getPassword()));
        } catch (Exception e) {
            log.debug(e.toString());
            return null;
        }
        log.debug("Authenticated!!");
        Optional<User> optional =  userRepo.findByUserEmail(loginRequestDTO.getUserEmail());
        if (optional.isEmpty()) return null;

        User user = optional.get();
        Boolean match = passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());
        if(!match) return null;

        UserDTO savUserDTO = modelMapper.map(user, UserDTO.class);
        user.setAuthority("USER");
        log.debug("User is ready so generatingToken");
        String token = jwt.generateToken(user);
        log.debug("Token is generated : {}",token);
        return new LoginResponseDTO(token, savUserDTO);
    }
}
