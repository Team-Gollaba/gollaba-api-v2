package org.tg.gollaba.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tg.gollaba.common.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_item_id", nullable = false)
    private PollItem pollItem;

    @Column // TODO: User 연관관계 매핑
    private Long userId;

    @Column(nullable = false)
    private String voterName;

    @Column(nullable = false)
    private String ipAddress;

    public Voter(PollItem pollItem,
                 Long userId,
                 String voterName,
                 String ipAddress) {
        this.pollItem = pollItem;
        this.userId = userId;
        this.voterName = voterName;
        this.ipAddress = ipAddress;
    }
}
