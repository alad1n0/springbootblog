package edu.itstep.final_project_v1.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String title;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category")
    private Set<Post> posts;
}
