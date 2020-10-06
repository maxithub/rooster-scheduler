package io.roosterscheduler.user.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class AppUser {
    private Long id;
    private String name;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private Instant createdAt;
    private Instant lastLoggedInAt;
    private String[] roles;
}
