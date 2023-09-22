package org.tg.gollaba.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.tg.gollaba.common.TestFixture;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.PollOption;
import org.tg.gollaba.poll.PollFixture;
import org.tg.gollaba.poll.PollOptionFixture;
import org.tg.gollaba.repository.PollRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PollServiceTest {
    private PollService service;
    private PollRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PollRepository.class);
        service = new PollService(repository);
    }

    @Test
    void create() {
        //given
        var requirement = new RequirementFixture().build();

        var poll = new PollFixture()
            .setPollType(Poll.PollType.ANONYMOUS)
            .build();

        List<PollOption> pollOptions = Arrays.asList(
            new PollOptionFixture().build(),
            new PollOptionFixture().build()
        );

        ReflectionTestUtils.setField(poll, "id", 1L);

        BDDMockito.given(repository.save(Mockito.any(Poll.class)))
                .willReturn(poll);

        //when
        poll.updatePollOptions(pollOptions);
        Throwable throwable = BDDAssertions
                .catchThrowable(() -> service.create(requirement));

        //then
        assertThat(throwable).isNull();
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static class RequirementFixture implements TestFixture<PollService.CreateRequirement> {
        private Long userId = 1L;

        String title = "title";

        String creatorName = "hamtori";

        Poll.PollType pollType = Poll.PollType.NAMED;

        Poll.PollResponseType responseType = Poll.PollResponseType.MULTI;

        List<PollService.CreateRequirement.PollOptionRequirement> pollOptions = new ArrayList<>();


        @Override
        public PollService.CreateRequirement build() {
            return new PollService.CreateRequirement(
                Optional.ofNullable(userId),
                title,
                creatorName,
                pollType,
                responseType,
                pollOptions
            );
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static class RequirementOptionFixture implements TestFixture<PollService.CreateRequirement.PollOptionRequirement> {
        String description = "description";
        String imageUrl = "imageUrl";

        @Override
        public PollService.CreateRequirement.PollOptionRequirement build() {
            return new PollService.CreateRequirement.PollOptionRequirement(description, imageUrl);
        }
    }
}