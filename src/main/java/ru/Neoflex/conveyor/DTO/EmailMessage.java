package ru.Neoflex.conveyor.DTO;

record EmailMessage(
        String address,
        Enum theme,
        Long applicationId
) {}
