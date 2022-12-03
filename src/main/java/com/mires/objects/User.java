package com.mires.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class User {
    private final UUID uuid;
    private String name;
    private String surname;
    private String email;
    private String login;
    private String password;
    private int role;
}

