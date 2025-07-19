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
    private Rank rank;

    private String assignedTo;
    private boolean completed;
}
