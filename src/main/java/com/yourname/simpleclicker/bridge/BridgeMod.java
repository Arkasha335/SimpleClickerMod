package com.yourname.simpleclicker.bridge;

public enum BridgeMode {
    DISABLED("Disabled"),
    GODBRIDGE("Godbridge"),
    NINJA("Ninja"),
    MOONWALK("Moonwalk"),
    BREEZILY("Breezily");

    private final String displayName;

    BridgeMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BridgeMode getNext() {
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }
}