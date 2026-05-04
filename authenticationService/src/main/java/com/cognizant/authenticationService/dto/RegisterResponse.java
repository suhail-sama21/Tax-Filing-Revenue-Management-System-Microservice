package com.cognizant.authenticationService.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class RegisterResponse {
    String name;
    String message;
}
