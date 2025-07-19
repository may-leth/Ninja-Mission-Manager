package com.may.Ninja.Mission.Manager.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "missions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_rank")
    private Rank rank;

    @Column(name = "assigned_to")
    private String assignedTo;
    private boolean completed;
}
