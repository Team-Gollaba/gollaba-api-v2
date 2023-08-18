package org.tg.gollaba.service;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.repository.PollRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PollServiceTest {
    private PollService service;
    private PollRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PollRepository.class);
        service = new PollService(repository);
    }

    @Test //객체 null 테스트
    void create() {
        //given
        List<PollService.PollOptionRequirement> pollOptionRequirements = new ArrayList<>();
        pollOptionRequirements.add(new PollService.PollOptionRequirement("test", "imgUrl"));

        var createRequest = new PollService.CreateRequirement(
                Optional.ofNullable(1L),
                "title",
                "hamtori",
                Poll.PollType.NAMED,
                Poll.PollResponseType.MULTI,
                pollOptionRequirements
        );

        var poll = new Poll(createRequest.toEntity().getUserId(), //이래야 null은 null로 들어감
                createRequest.toEntity().getTitle(), //반환되어서 저장되는 건 entity로 변경되는 칭구여야 함
                createRequest.toEntity().getCreatorName(),
                createRequest.toEntity().getPollType(),
                createRequest.toEntity().getResponseType());

        var pollOptions = PollService.PollOptionRequirement
                .createPollOptions(createRequest, poll);

        ReflectionTestUtils.setField(poll, "id", 1L);

        BDDMockito.given(repository.save(Mockito.any(Poll.class)))
                .willReturn(poll);

        //when
        service.addPollOptions(poll, pollOptions);
        Throwable throwable = BDDAssertions
                .catchThrowable(() -> service.create(createRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void pollOptionValidate() {
        //given
        List<PollService.PollOptionRequirement> pollOptionRequirements = new ArrayList<>();
        pollOptionRequirements.add(new PollService.PollOptionRequirement("test", "imgUrl"));
        pollOptionRequirements.add(new PollService.PollOptionRequirement("test2", "imgUrl2"));

        var createRequest = new PollService.CreateRequirement(
                Optional.ofNullable(1L),
                "title",
                "hamtori",
                Poll.PollType.NAMED,
                Poll.PollResponseType.MULTI,
                pollOptionRequirements);

        var poll = new Poll(createRequest.toEntity().getUserId(),
                createRequest.toEntity().getTitle(),
                createRequest.toEntity().getCreatorName(),
                createRequest.toEntity().getPollType(),
                createRequest.toEntity().getResponseType());

        ReflectionTestUtils.setField(poll, "id", 1L);

        BDDMockito.given(repository.save(Mockito.any(Poll.class)))
                .willReturn(poll);

        //when
        service.create(createRequest);

        //then
        var argumentCaptor = ArgumentCaptor.forClass(Poll.class);
        verify(repository, times(1))
                .save(argumentCaptor.capture());
        var capturedPoll = argumentCaptor.getValue();

        var optionSize = capturedPoll.getOptions().size();

        assertThat(optionSize)
                .as("항목은 최소 2개부터 최대 10개까지 입니다")
                .isBetween(2, 10);
    }

    @Test
    void pollEndedAtTest() {
        //given
        List<PollService.PollOptionRequirement> pollOptionRequirements = new ArrayList<>();

        var createRequest = new PollService.CreateRequirement(
                Optional.ofNullable(1L),
                "title",
                "hamtori",
                Poll.PollType.NAMED,
                Poll.PollResponseType.MULTI,
                pollOptionRequirements);

        var poll = new Poll(createRequest.toEntity().getUserId(),
                createRequest.toEntity().getTitle(),
                createRequest.toEntity().getCreatorName(),
                createRequest.toEntity().getPollType(),
                createRequest.toEntity().getResponseType());

        ReflectionTestUtils.setField(poll, "id", 1L);

        BDDMockito.given(repository.save(Mockito.any(Poll.class)))
                .willReturn(poll);

        //when
        service.create(createRequest);

        //then
        var argumentCaptor = ArgumentCaptor.forClass(Poll.class);
        verify(repository, times(1))
                .save(argumentCaptor.capture());
        var capturedPoll = argumentCaptor.getValue();

        assertThat(capturedPoll.getEndedAt()
                .isEqual(capturedPoll.createdAt().plusDays(7)))
                .isTrue(); //주어진 조건이 true인가?
    }
}