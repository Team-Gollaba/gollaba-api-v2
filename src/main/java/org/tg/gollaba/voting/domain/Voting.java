package org.tg.gollaba.voting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
public class Voting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pollId;

    private Long userId;

    @Embedded
    private VoterName voterName;

    @Column
    private LocalDateTime deletedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "voting_id", nullable = false)
    private Set<VotingItem> items = new HashSet<>();

    public Voting(Long pollId,
                         Long userId,
                         VoterName voterName,
                         Set<VotingItem> items) {
        this.pollId = pollId;
        this.userId = userId;
        this.voterName = voterName;
        this.items = items;
    }

    public void update(VoterName voterName,
                       Set<VotingItem> items) {
        this.voterName = voterName;
        setItems(items);
    }

    private void setItems(Set<VotingItem> items) {
        this.items.removeIf(existingItem -> !items.contains(existingItem));
        this.items.addAll(items);
    }

    public void cancel(){
        this.deletedAt = LocalDateTime.now();
    }
}
