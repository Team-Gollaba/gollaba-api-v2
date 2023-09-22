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
    private List<PollItem> items = new ArrayList<>();

    public enum PollResponseType {
        SINGLE,
        MULTI
    }

    public enum PollType {
        NAMED,
        ANONYMOUS
    }

    public Poll(Long userId, //TODO create 시 endedAt을 받아오도록
                String title,
                String creatorName,
                PollType pollType,
                PollResponseType responseType,
                LocalDateTime endedAt
    ){
        this.userId = userId;
        this.title = title;
        this.creatorName = creatorName;
        this.pollType = pollType;
        this.responseType = responseType;
        this.endedAt = setEndedAt(endedAt);
        this.readCount = 0;
    }

    private LocalDateTime setEndedAt(LocalDateTime endedAt){
        return (endedAt == null)
            ? LocalDateTime.now().plusDays(7)
            : endedAt;
    }

    public void updatePollOptions(List<PollItem> pollItems) {
            if(pollItems.size() < 2 || pollItems.size() > 10){
                throw new InvalidOptionSizeException();
            }
        this.items.addAll(pollItems);
        }
    public static class InvalidOptionSizeException extends IllegalArgumentException {
        public InvalidOptionSizeException() { //이름을 그대로 사용해야함
            super("지정할 수 있는 항목의 범위가 아닙니다.");
        }
    }
}
