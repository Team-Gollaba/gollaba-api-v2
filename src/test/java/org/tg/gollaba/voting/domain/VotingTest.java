package org.tg.gollaba.voting.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VotingTest {
    @Test
    void updateTest() {
        //given
        var voting = new VotingFixture().build();
        var newName = new VoterNameFixture()
            .setValue("newName")
            .build();
        var newItems = Set.of(
            new VotingItem(1L),
            new VotingItem(2L)
        );

        //when
        voting.update(newName, newItems);

        //then
        assertThat(voting.voterName()).isEqualTo(newName);
        assertThat(voting.items()).isEqualTo(newItems);
    }
}