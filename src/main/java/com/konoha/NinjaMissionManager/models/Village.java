package com.konoha.NinjaMissionManager.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "villages")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Village {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kage_id", nullable = false)
    private Ninja kage;
}
