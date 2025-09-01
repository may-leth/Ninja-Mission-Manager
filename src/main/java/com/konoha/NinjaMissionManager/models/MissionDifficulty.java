package com.konoha.NinjaMissionManager.models;

public enum MissionDifficulty {
    D,
    C,
    B,
    A,
    S;

    public boolean isHighRank() {
        return this == A || this == S;
    }
}