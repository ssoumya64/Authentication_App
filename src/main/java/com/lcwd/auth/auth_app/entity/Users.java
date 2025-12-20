package com.lcwd.auth.auth_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;
    @Column(name = "user_email", unique = true, length = 300)
    @Email
    private String email;
    private String username;
    private String password;
    private boolean enable=true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    @Enumerated(EnumType.STRING)
    private Provider provider=Provider.LOCAL;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name ="roles_id"))
    private Set<Roles> roles=new HashSet<>();

    @PrePersist
    protected void onCreate(){
      Instant now=Instant.now();
      if(createdAt == null) createdAt = now;
      updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt=Instant.now();
    }
}
