package org.tg.gollaba.voting.vo;

import org.tg.gollaba.voting.domain.Voting;
import org.tg.gollaba.voting.domain.VotingItem;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record VotingVo(
    Long id,
    Long pollId,
    Long userId,
    String voterName,
    LocalDateTime deletedAt,
    Set<Item> items
) {
    public static VotingVo from(Voting voting) {
        return new VotingVo(
            voting.id(),
            voting.pollId(),
            voting.userId(),
            voting.voterName().value(),
            voting.deletedAt(),
            voting.items().stream()
                .map(Item::from)
                .collect(Collectors.toSet())
        );
    }

    public record Item(
        Long id,
        Long pollItemId
    ) {
        public static Item from(VotingItem votingItem) {
            return new Item(
                votingItem.id(),
                votingItem.pollItemId()
            );
        }
    }
}