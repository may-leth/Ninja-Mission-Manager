package com.konoha.NinjaMissionManager.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    @Column(nullable = false)
    private Integer missionsCompletedCount;

    @Column(nullable = false)
    private boolean isAnbu;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "ninja_roles", joinColumns = @JoinColumn(name = "ninja_id"))
    private Set<Role> roles;
}