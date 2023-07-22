package ru.Neoflex.conveyor.Enum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MaterialStatus {
    POOR("Poor"),
    MIDDLE_CLASS("Middle class"),
    FUNDED("Funded"),
    WELL_ENDOWED("Well endowed");

    private final String materialStatus;
}
