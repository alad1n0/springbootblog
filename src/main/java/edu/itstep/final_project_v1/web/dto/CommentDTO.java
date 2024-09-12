package edu.itstep.final_project_v1.web.dto;

import edu.itstep.final_project_v1.domain.models.Comment;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
public class CommentDTO {

    private Long id;
    private String text;
    private String accountFirstName;
    private String formattedDate;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.accountFirstName = comment.getAccount().getFirstName();

        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy / HH:mm", Locale.ENGLISH);
        this.formattedDate = comment.getCreatedAt().format(originalFormatter);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", accountFirstName=" + accountFirstName +
                ", formattedDate='" + formattedDate + '\'' +
                '}';
    }
}