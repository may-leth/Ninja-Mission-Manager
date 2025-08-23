package com.konoha.NinjaMissionManager.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ninjas")
@Data
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

    @ManyToOne
    @JoinColumn(name = "village_id")
    private Village village;

    @Builder.Default
    @Column(nullable = false)
    private Integer missionsCompletedCount = 0;

    @Column(nullable = false)
    private boolean isAnbu;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "ninja_roles", joinColumns = @JoinColumn(name = "ninja_id"))
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ninja_missions",
            joinColumns = @JoinColumn(name = "ninja_id"),
            inverseJoinColumns = @JoinColumn(name = "mission_id")
    )

    @Builder.Default
    private Set<Mission> assignedMissions = new HashSet<>();
}