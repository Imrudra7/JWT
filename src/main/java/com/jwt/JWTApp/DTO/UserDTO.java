package com.jwt.JWTApp.DTO;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {

    private Long userId;
    private String userEmail;
    private String userName;

}
