package org.tg.gollaba.voting.application;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.poll.application.PollRepository;
import org.tg.gollaba.poll.domain.PollFixture;
import org.tg.gollaba.poll.domain.PollItemFixture;
import org.tg.gollaba.voting.domain.VotingFixture;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UpdateVotingServiceTest {
    @InjectMocks
    private UpdateVotingService service;
    @Mock
    private VotingRepository votingRepository;
    @Mock
    private VotingValidator votingValidator;
    @Mock
    private PollRepository pollRepository;


    @Test
    void success() {
        //given
        var requirement = new RequirementFixture()
            .setPollItemIds(Set.of(1L))
            .build();
        var voting = new VotingFixture().build();
        var poll = new PollFixture()
            .setItems(List.of(
                new PollItemFixture().setId(1L).build(),
                new PollItemFixture().setId(2L).build()
            ))
            .build();

        given(votingRepository.findById(requirement.votingId()))
            .willReturn(Optional.of(voting));
        given(pollRepository.findById(voting.pollId()))
            .willReturn(Optional.of(poll));

        //when
        var throwable = catchThrowable(() -> service.update(requirement));

        //then
        assertThat(throwable).isNull();
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class RequirementFixture implements TestFixture<UpdateVotingService.Requirement> {
        private Long votingId = 1L;
        private String voterName = "John Doe";
        private Set<Long> pollItemIds = Set.of(1L);

        @Override
        public UpdateVotingService.Requirement build() {
            return new UpdateVotingService.Requirement(
                votingId,
                Optional.of(voterName),
                Optional.of(pollItemIds)
            );
        }
    }
}