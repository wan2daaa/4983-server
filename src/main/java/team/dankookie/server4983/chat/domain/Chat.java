package team.dankookie.server4983.chat.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import team.dankookie.server4983.chat.constant.ContentType;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ColumnDefault("false")
    private Boolean isRead = false;

    @Column(nullable = false)
    private String message;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    enum UserType {
        BUYER, SELLER
    }
}
