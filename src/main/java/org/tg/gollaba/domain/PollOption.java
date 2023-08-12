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
public class PollOption extends BaseEntity {

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

    //@OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "pollOption", cascade = CascadeType.ALL)
    private Set<Voter> voters = new HashSet<>(); //이거 중복 방지라 hash?

    public PollOption(Poll poll,
                      String description,
                      String imageUrl) {
        this.poll = poll;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
