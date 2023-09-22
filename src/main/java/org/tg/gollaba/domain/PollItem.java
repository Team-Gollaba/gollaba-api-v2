package org.tg.gollaba.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tg.gollaba.common.entity.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(nullable = false)
    private String description;

    @Column
    private String imageUrl;

    @OneToMany(mappedBy = "pollItem", cascade = CascadeType.ALL)
    private Set<Voter> voters = new HashSet<>();

    public PollItem(Poll poll,
                      String description,
                      String imageUrl) {
        this.poll = poll;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
