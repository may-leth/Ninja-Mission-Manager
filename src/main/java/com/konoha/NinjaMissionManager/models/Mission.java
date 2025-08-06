package com.konoha.NinjaMissionManager.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "missions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    int reward;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_difficulty")
    MissionDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status;

    @Column(nullable = false)
    LocalDateTime creationDate;

    @ManyToMany(mappedBy = "assignedMissions")
    private Set<Ninja> assignedNinjas = new HashSet<>();
}