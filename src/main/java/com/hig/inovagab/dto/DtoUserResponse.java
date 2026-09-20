package com.hig.inovagab.dto;

import com.hig.inovagab.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DtoUserResponse {
    private String id;
    private String name;
    private String email;
    private UserRole role;
}