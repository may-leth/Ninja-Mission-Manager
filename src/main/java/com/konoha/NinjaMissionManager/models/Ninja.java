package com.konoha.NinjaMissionManager.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ninjas")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Ninja {
    @EqualsAndHashCode.Include
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

    @JsonBackReference
    @ManyToMany(mappedBy = "assignedNinjas", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Mission> assignedMissions = new HashSet<>();
}