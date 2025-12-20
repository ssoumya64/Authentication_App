package com.lcwd.auth.auth_app.dtos;

import com.lcwd.auth.auth_app.entity.Provider;
import com.lcwd.auth.auth_app.entity.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtos {
    private UUID id;

    private String email;
    private String username;
    private String password;
    private boolean enable=true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    //    private String gender;
//    private Address address;
    private Provider provider=Provider.LOCAL;

    private Set<RolesDtos> roles=new HashSet<>();
}
