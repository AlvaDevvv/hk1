package com.oreofactory.oreofactory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RegisterResponseDTO {
    private int id;
    private String username;
    private String email;
    private String role;
    private String branch;
    private LocalDateTime createdAt;


}
