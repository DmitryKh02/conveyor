package ru.Neoflex.conveyor.Enum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MaterialStatus {
    SINGLE("Single"),
    DIVORCED("Divorced"),
    MARRIED("Married");

    private final String materialStatus;
}
