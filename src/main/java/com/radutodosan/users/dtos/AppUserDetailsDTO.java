package com.radutodosan.users.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserDetailsDTO {
    private String username;
    private String email;
}
