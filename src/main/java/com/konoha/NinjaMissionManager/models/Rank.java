package com.konoha.NinjaMissionManager.models;

public enum Rank {
    GENIN,
    CHUNIN,
    JONIN,
    KAGE;

    public boolean isAbove(Rank otherRank) {
        return this.ordinal() > otherRank.ordinal();
    }
}
