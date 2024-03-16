package org.tg.gollaba.poll.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Poll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
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
    private LocalDateTime endAt;

    @Column(nullable = false)
    private Integer readCount;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "poll_id", nullable = false)
    @OrderColumn(name = "position")
    private List<PollItem> items = new ArrayList<>();

    public Poll(Long userId,
                String title,
                String creatorName,
                PollResponseType responseType,
                PollType pollType,
                LocalDateTime endAt,
                List<PollItem> items) {
        this.userId = userId;
        this.title = title;
        this.creatorName = creatorName;
        this.responseType = responseType;
        this.pollType = pollType;
        setItems(items);
        setEndAt(endAt);
        this.readCount = 0;
    }

    private void setItems(List<PollItem> items) {
        if (items.size() < 2) {
            throw new IllegalArgumentException("투표 항목은 최소 두 개 이상이어야 합니다.");
        }

        this.items = items;
    }

    private void setEndAt(LocalDateTime endAt) {
        if (endAt == null) {
            this.endAt = LocalDateTime.now()
                .plusWeeks(1)
                .with(LocalTime.MAX);
            return;
        }

        this.endAt = endAt;
    }

    public PollItem getItem(long itemId) {
        return items.stream()
          .filter(item -> item.id().equals(itemId))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 항목입니다."));
    }

    public List<PollItem> getItems(Collection<Long> itemIds) {
        return items.stream()
          .filter(item -> itemIds.contains(item.id()))
          .toList();
    }

    public enum PollResponseType {
        SINGLE,
        MULTIPLE
    }

    public enum PollType {
        NAMED,
        ANONYMOUS
    }
}
