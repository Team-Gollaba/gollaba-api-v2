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
@NoArgsConstructor
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
    private Integer readCount;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    @OrderColumn(name = "position")
    private List<PollOption> options = new ArrayList<>();

    public enum PollResponseType {
        SINGLE,
        MULTI
    }

    public enum PollType {
        NAMED,
        ANONYMOUS
    }

    public PollOption getItem(long itemId) {
        return options.stream()
          .filter(item -> item.getId() == itemId)
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 항목입니다."));
    }
}
