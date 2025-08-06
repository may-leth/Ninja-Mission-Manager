package com.konoha.NinjaMissionManager.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ninjas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Ninja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "ninja_rank")
    private Rank rank;

    @Column(nullable = false)
    private String village; //recuerda cambiar por entidad

    @Column(nullable = false)
    private Integer missionsCompletedCount;

    @Column(nullable = false)
    private boolean isAnbu;
}
