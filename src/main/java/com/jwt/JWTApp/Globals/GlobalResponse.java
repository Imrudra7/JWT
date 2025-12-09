package com.jwt.JWTApp.Globals;

import lombok.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class GlobalResponse {
    private HttpStatusCode code;
    private String message;
    private Object data;

}
