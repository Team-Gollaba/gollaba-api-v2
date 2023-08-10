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

    @Column(nullable = false)
    private Boolean isBallot;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private Integer readCount;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    @OrderColumn(name = "position")
    private List<PollOption> options = new ArrayList<>();

    public enum PollResponseType {
        SINGLE,
        MULTI
    }
}
