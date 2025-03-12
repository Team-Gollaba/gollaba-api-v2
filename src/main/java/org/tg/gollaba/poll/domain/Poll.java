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

import static java.time.LocalDateTime.now;

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

        if (items.size() > 6) {
            throw new IllegalArgumentException("투표 항목은 최대 6개까지 추가할 수 있습니다.");
        }

        this.items = items;
    }

    private void setEndAt(LocalDateTime endAt) {
        if (endAt != null) {
            if (now().plusMinutes(30).isAfter(endAt)) {
                throw new IllegalArgumentException("투표 유효기간은 현재 시간보다 30분 이상이어야 합니다.");
            }
        }

        if (endAt == null) {
            this.endAt = now()
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

    public void increaseReadCount(){
        this.readCount += 1;
    }

    public void update(String title, LocalDateTime endAt) {
        assert title != null;
        setEndAt(endAt);

        this.title = title;
        this.endAt = endAt;
    }

    public void updatePollItem(Long pollItemId,
                               String description,
                               String imageUrl){
        var pollItem = getItem(pollItemId);
        assert description != null;

        pollItem.changeDescription(description);
        pollItem.changeImageUrl(imageUrl);
    }

    public enum PollResponseType {
        SINGLE,
        MULTIPLE
    }

    public enum PollResponseTypeB {
        SINGLE,
        MULTIPLE
    }

    public enum PollType {
        NAMED,
        ANONYMOUS
    }
}
