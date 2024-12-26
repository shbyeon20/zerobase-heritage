package com.zerobase.zerobaseheritage.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
    private String memberId;
    private String password;
}
