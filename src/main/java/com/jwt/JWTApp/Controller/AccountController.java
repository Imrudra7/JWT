package com.jwt.JWTApp.Controller;

import com.jwt.JWTApp.DTO.AddUserRequestDTO;
import com.jwt.JWTApp.DTO.LoginRequestDTO;
import com.jwt.JWTApp.DTO.LoginResponseDTO;
import com.jwt.JWTApp.DTO.UserDTO;
import com.jwt.JWTApp.Globals.GlobalResponse;
import com.jwt.JWTApp.Service.AccountService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/account")
@RestController
public class AccountController {
    private final AccountService accountService;
    
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/addUser")
    GlobalResponse  addUser(@RequestBody AddUserRequestDTO addUserRequestDTO) {
        log.debug("Inside addUser Controller.", addUserRequestDTO.toString());
        UserDTO obj = accountService.addUser(addUserRequestDTO);
        if(null != obj)
        log.debug("Got response from service after addUser : {}",obj.toString());

        if (obj == null)
            return new GlobalResponse(HttpStatusCode.valueOf(500),"User Already present with this email.",addUserRequestDTO.getUserEmail());
        else
            return new GlobalResponse(HttpStatusCode.valueOf(200), "User created successfully", obj);
    }
    @PostMapping("/login")
    ResponseEntity<GlobalResponse>  login(@RequestBody LoginRequestDTO loginRequestDTO) {
        log.debug("Inside login Controller.", loginRequestDTO.toString());
        LoginResponseDTO obj = accountService.login(loginRequestDTO);
        if(null != obj)
        log.debug("Got response from service after login : {}",obj.toString());
        ResponseEntity<GlobalResponse> response ;
        if (obj == null)
          response = new ResponseEntity<>( new GlobalResponse(HttpStatusCode.valueOf(500),"Please put correct email id or password",loginRequestDTO.getUserEmail()),HttpStatusCode.valueOf(500));
        else
            response = new ResponseEntity<>(new GlobalResponse(HttpStatusCode.valueOf(200), "Logged in successfully.", obj), HttpStatus.OK);
        return response;
    }

    @PostMapping("/getUser")
    GlobalResponse  getUser(@RequestBody UserDTO userDTO) {
        log.debug("Inside getUser Controller.");
        UserDTO obj = accountService.getUser(userDTO);
        if(null != obj)
        log.debug("Got USERDTO from service {}",obj.toString());
        if (obj == null)
            return new GlobalResponse(HttpStatusCode.valueOf(500),"User not present with this email.",userDTO.getUserEmail());
        else
            return new GlobalResponse(HttpStatusCode.valueOf(200), "User fetched successfully", obj);
    }
}
