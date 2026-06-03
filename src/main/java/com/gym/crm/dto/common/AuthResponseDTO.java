package com.gym.crm.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AuthResponseDTO {
    private String username;
    private String token;
}
