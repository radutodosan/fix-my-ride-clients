package com.radutodosan.clients.dtos;

import com.radutodosan.clients.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Client client;
}
