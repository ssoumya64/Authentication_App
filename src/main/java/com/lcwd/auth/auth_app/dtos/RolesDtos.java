package com.lcwd.auth.auth_app.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesDtos {
    private UUID id= UUID.randomUUID();

    private String name;
}
