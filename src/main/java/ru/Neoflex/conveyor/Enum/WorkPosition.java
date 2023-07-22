package ru.Neoflex.conveyor.Enum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WorkPosition {
    MIDDLE_MANAGER("Middle manager"),
    TOP_MANAGER("Top manager");

    private final String workPosition;
}
