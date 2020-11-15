package my.learning.springbootbatch.domain;

import lombok.*;
import my.learning.springbootbatch.domain.enums.Grade;
import my.learning.springbootbatch.domain.enums.SocialType;
import my.learning.springbootbatch.domain.enums.UserStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = {"idx", "email"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idx;
    private String name;
    private String password;
    private String email;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private String principal;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    public User setInactive() {
        this.status = UserStatus.INACTIVE;
        return this;
    }


}