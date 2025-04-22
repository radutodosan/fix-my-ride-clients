package com.radutodosan.users.dtos;

import com.radutodosan.users.entities.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private AppUser user;
}
