package com.jwt.JWTApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponseDTO {

   private String token;
   private UserDTO userDTO;
}
