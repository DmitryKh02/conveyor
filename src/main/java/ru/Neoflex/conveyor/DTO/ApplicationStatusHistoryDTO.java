package ru.Neoflex.conveyor.DTO;

import java.time.LocalDateTime;

record ApplicationStatusHistoryDTO(
        Enum status,
        LocalDateTime time,
        Enum changeType
) {}
