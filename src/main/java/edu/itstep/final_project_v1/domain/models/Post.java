package edu.itstep.final_project_v1.domain.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String title;

    @Column(columnDefinition = "TEXT")
    String body;

    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    @JsonManagedReference
    Account account;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = true)
    @JsonManagedReference
    Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Rating> ratings;

    @Setter
    @Getter
    @Transient
    private double averageRating;

    @Setter
    @Getter
    @Transient
    private long likeCount;

    @Setter
    @Getter
    @Transient
    private String formattedDate;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = true)
    private String imageBase64;
}