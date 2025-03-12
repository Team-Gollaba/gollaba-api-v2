package org.tg.gollaba.poll.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.common.web.PageResponse;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.vo.PollSummary;

public abstract class HashIdController {
    private final HashIdHandler hashIdHandler;

    protected HashIdController(HashIdHandler hashIdHandler) {
        this.hashIdHandler = hashIdHandler;
    }

    protected String createHashId(Long pollId) {
        return hashIdHandler.encode(pollId);
    }

    protected long getPollId(String pollHashId) {
        long result;

        try {
            result = hashIdHandler.decode(pollHashId);
        } catch (HashIdHandler.FailToDecodeHashIdException e) {
            throw new BadRequestException(Status.INVALID_POLL_HASH_ID);
        }

        return result;
    }

    protected List<PollSummaryResponse> convertToResponse(List<PollSummary> pollSummaries) {
        return pollSummaries.stream()
            .map(pollSummary -> PollSummaryResponse.toResponse(pollSummary, hashIdHandler))
            .toList();
    }

    protected PageResponse<PollSummaryResponse> convertToResponse(PageResponse<PollSummary> pollSummaries) {
        return new PageResponse<>(
            pollSummaries.getItems().stream()
                .map(pollSummary -> PollSummaryResponse.toResponse(pollSummary, hashIdHandler))
                .toList(),
            pollSummaries.getPage(),
            pollSummaries.getSize(),
            pollSummaries.getTotalCount(),
            pollSummaries.getTotalPage()
        );
    }
    protected record PollSummaryResponse(
        String id,
        String title,
        String creatorName,
        String creatorProfileUrl,
        Poll.PollResponseType responseType,
        Poll.PollType pollType,
        LocalDateTime endAt,
        Integer readCount,
        Integer totalVotingCount,
        Integer votedPeopleCount,
        List<PollSummary.PollItem> items
    ) {
        public static PollSummaryResponse toResponse(PollSummary pollSummary, HashIdHandler hashIdHandler) {
            return new PollSummaryResponse(
                hashIdHandler.encode(pollSummary.id()),
                pollSummary.title(),
                pollSummary.creatorName(),
                pollSummary.creatorProfileUrl(),
                pollSummary.responseType(),
                pollSummary.pollType(),
                pollSummary.endAt(),
                pollSummary.readCount(),
                pollSummary.totalVotingCount(),
                pollSummary.votedPeopleCount(),
                pollSummary.items()
            );
        }
    }
}
