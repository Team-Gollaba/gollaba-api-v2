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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        var createRequest = new PollService.CreateRequest(
                Optional.ofNullable(1L),
                "title",
                "hamtori",
                Poll.PollType.NAMED,
                Poll.PollResponseType.MULTI);

        var poll = new Poll(createRequest.toEntity().getUserId(), //이래야 null은 null로 들어감
                createRequest.toEntity().getTitle(), //반환되어서 저장되는 건 entity로 변경되는 칭구여야 함
                createRequest.toEntity().getCreatorName(),
                createRequest.toEntity().getPollType(),
                createRequest.toEntity().getResponseType());
        ReflectionTestUtils.setField(poll, "id", 1L);

        BDDMockito.given(repository.save(Mockito.any(Poll.class)))
                .willReturn(poll);

        //when
        Throwable throwable = BDDAssertions
                .catchThrowable(() -> service.create(createRequest));

        //then
        assertThat(throwable).isNull();
    }


    @Test //필드 테스트
    void fieldTest() {
        //given
        var createRequest = new PollService.CreateRequest(
                Optional.ofNullable(null),
                "title",
                "hamtori",
                Poll.PollType.NAMED,
                Poll.PollResponseType.MULTI);

        var poll = new Poll(createRequest.toEntity().getUserId(), //이래야 null은 null로 들어감
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
        var capturedTodo = argumentCaptor.getValue();

        assertThat(capturedTodo.getUserId()).isEqualTo(null);
        assertThat(capturedTodo.getTitle()).isEqualTo("title");
        assertThat(capturedTodo.getPollType()).isEqualTo(Poll.PollType.NAMED);
        assertThat(capturedTodo.getResponseType()).isEqualTo(Poll.PollResponseType.MULTI);
        assertThat(capturedTodo.getEndedAt()).isEqualTo(capturedTodo.createdAt().plusDays(7));
        assertThat(capturedTodo.getReadCount()).isEqualTo(0);
    }
}