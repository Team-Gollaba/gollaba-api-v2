package org.tg.gollaba.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tg.gollaba.common.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Poll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column // TODO: User 연관관계 매핑
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String creatorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PollResponseType responseType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PollType pollType;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private Integer readCount; //조회수

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    @OrderColumn(name = "position")
    private List<PollOption> options = new ArrayList<>();

    public enum PollResponseType {
        SINGLE,
        MULTI
    }

    public enum PollType {
        NAMED,
        ANYMOUS
    }

    public Poll(Long userId,
                String title,
                String creatorName,
                PollType pollType,
                PollResponseType responseType
    ){
        this.userId = userId;
        this.title = title;
        this.creatorName = creatorName;
        this.pollType = pollType;
        this.responseType = responseType;
        this.endedAt = createdAt().plusDays(7);
        this.readCount = 0;
    }
}
