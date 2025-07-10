package net.softhem.pos.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommandDTO {
    private Long id;
    private String command;
    private String status;
}