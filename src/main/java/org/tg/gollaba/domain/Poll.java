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
        ANONYMOUS
    }

    public Poll(Long userId,
                String title,
                String creatorName,
                PollType pollType,
                LocalDateTime endedAt,
                PollResponseType responseType
    ){
        this.userId = userId;
        this.title = title;
        this.creatorName = creatorName;
        this.pollType = pollType;
        this.responseType = responseType;
        this.endedAt = setEndedAt(endedAt)
        this.readCount = 0;
    }

    private void setEndedAt(LocalDateTime endedAt) {
        return endedAt == null 
            ? LocalDateTime.now().plusDays(7)
            : endedAt;
    }

    public void updatePollOptions(List<PollOption> options) {
        if (options.size() < 2 || options.size() > 10) {
            throw new InvalidOptionSizeException();
        }

        this.options.addAll(options);
    }

    static class InvalidOptionSizeException extends IllegalArgumentException {
        public InvalidOptionSizeException() { //이름을 그대로 사용해야함
            super("지정할 수 있는 항목의 범위가 아닙니다.");
        }
    }
}
