package sg.edu.nus.journybackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("customers")
public class Customer {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    private String password;
    private String name;
    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    private List<Comment> comments;
    @JsonIgnore
    private List<Post> posts;
}
