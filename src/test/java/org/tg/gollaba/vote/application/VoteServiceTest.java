package org.tg.gollaba.vote.application;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.poll.domain.PollOptionFixture;
import org.tg.gollaba.repository.PollRepository;
import org.tg.gollaba.vote.application.VoteService;
import org.tg.gollaba.vote.infrastructure.VoterRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {
    @InjectMocks
    private VoteService service;
    @Mock
    private PollRepository pollRepository;
    @Mock
    private VoterRepository voterRepository;

    @DisplayName("투표 요청을 처리한다.")
    @Test
    void success() {
        // given
        var requirement = new RequirementFixture().build();
        var pollOption = new PollOptionFixture()
            .setId(requirement.pollOptionId())
            .build();
        BDDMockito.given(pollRepository.findByPollOptionId(eq(requirement.pollOptionId())))
            .willReturn(Optional.of(pollOption));

        // when
        var throwable = Assertions.catchThrowable(() -> service.vote(requirement));

        // then
        assertThat(throwable).isNull();
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static class RequirementFixture implements TestFixture<VoteService.Requirement> {
        private Long pollId = 1L;
        Long pollOptionId = 1L;
        Optional<Long> userId = Optional.empty();
        Optional<String> voterName = Optional.of("voterName");

        @Override
        public VoteService.Requirement build() {
            return new VoteService.Requirement(
                pollOptionId,
                userId,
                voterName
            );
        }
    }
}