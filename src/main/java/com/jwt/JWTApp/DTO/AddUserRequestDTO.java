package com.jwt.JWTApp.DTO;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddUserRequestDTO {

    private String userEmail;
    private String userName;
    private String password;
}
