package com.radutodosan.clients.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDetailsDTO {
    private String username;
    private String email;
    private String pictureUrl;
}
