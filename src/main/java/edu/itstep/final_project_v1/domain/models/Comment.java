package edu.itstep.final_project_v1.domain.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(columnDefinition = "TEXT")
    String text;

    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    @JsonManagedReference
    Post post;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    @JsonManagedReference
    Account account;

    @Setter
    @Getter
    @Transient
    private String formattedDate;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", postId=" + (post != null ? post.getId() : null) +
                ", accountId=" + (account != null ? account.getId() : null) +
                '}';
    }
}
