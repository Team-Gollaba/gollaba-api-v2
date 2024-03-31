package org.tg.gollaba.poll.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.function.Function.identity;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.tg.gollaba.common.support.QueryDslUtils.createColumnOrder;
import static org.tg.gollaba.poll.domain.QPoll.poll;
import static org.tg.gollaba.voting.domain.QVoting.voting;
import static org.tg.gollaba.voting.domain.QVotingItem.votingItem;

@RequiredArgsConstructor
public class PollRepositoryCustomImpl implements PollRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PollSummary> findPollList(GetPollListService.Requirement requirement) {
        var pageable = requirement.pageable();
        var where = Stream.of(
                requirement.optionGroup()
                    .map(optionGroup -> switch (optionGroup) {
                        case TITLE -> poll.title.contains(requirement.query().orElseThrow());
                    }),
                requirement.isActive()
                    .filter(Boolean.TRUE::equals)
                    .map(isEnded -> poll.endAt.goe(now())),
                requirement.pollType()
                    .map(poll.pollType::eq)
            )
            .flatMap(Optional::stream)
            .toArray(Predicate[]::new);
        var orderBy = pageable.getSort()
            .stream()
            .map(order -> switch (order.getProperty()) {
                case "createdAt" -> createColumnOrder(poll.createdAt, order);
                case "endAt" -> createColumnOrder(poll.endAt, order);
                default -> throw new IllegalArgumentException("존재하지 않는 컬럼입니다.");
            })
            .toArray(OrderSpecifier[]::new);

        var totalCount = queryFactory
            .select(poll.countDistinct())
            .from(poll)
            .where(where)
            .fetchOne();

        if (totalCount == null || totalCount == 0) {
            return Page.empty();
        }

        var polls = queryFactory
            .selectFrom(poll)
            .where(where)
            .orderBy(orderBy)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        var pollIds = polls.stream().map(Poll::id).toList();
        var votingCountMapByPollId = votingCountMapByPollId(pollIds);

        return new PageImpl<>(
            convert(polls, votingCountMapByPollId),
            pageable,
            totalCount
        );
    }

    @Override
    public GetPollDetailsService.PollDetails findPollDetails(Long id) {
        var pollEntity = queryFactory
            .selectFrom(poll)
            .where(poll.id.eq(id))
            .fetchOne();

        if (pollEntity == null) {
            throw new BadRequestException(Status.POLL_NOT_FOUND);
        }

        var votingCountMap = votingCountMap(pollEntity.id());
        var totalVotingCount = votingCountMap.values()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();

        return new GetPollDetailsService.PollDetails(
            pollEntity.id(),
            pollEntity.title(),
            pollEntity.creatorName(),
            pollEntity.responseType(),
            pollEntity.pollType(),
            pollEntity.endAt(),
            totalVotingCount,
            pollEntity.items()
                .stream()
                .map(item -> new GetPollDetailsService.PollDetails.PollItem(
                    item.id(),
                    item.description(),
                    item.imageUrl(),
                    votingCountMap.getOrDefault(item.id(), 0)
                ))
                .toList()
        );
    }

    private Map<Long, Map<Long, Integer>> votingCountMapByPollId(List<Long> pollIds) {
        return queryFactory
            .select(voting.pollId, votingItem.pollItemId, votingItem.count())
            .from(voting)
            .join(voting.items, votingItem)
            .where(voting.pollId.in(pollIds))
            .groupBy(votingItem.pollItemId)
            .fetch()
            .stream()
            .collect(
                Collectors.groupingBy(
                    t -> t.get(voting.pollId),
                    Collectors.toMap(
                        t -> t.get(votingItem.pollItemId),
                        t -> Optional.ofNullable(t.get(votingItem.count()))
                            .map(Long::intValue)
                            .orElse(0)
                    )
                )
            );
    }

    private Map<Long, Integer> votingCountMap(long pollId) {
        var result = votingCountMapByPollId(List.of(pollId))
            .get(pollId);

        return result == null ? Collections.emptyMap() : result;
    }

    private List<PollSummary> convert(List<Poll> polls,
                                                         Map<Long, Map<Long, Integer>> votingCountMapByPollId) {
        return polls.stream()
            .map(poll -> {
                var votingCountMap = votingCountMapByPollId.getOrDefault(poll.id(), emptyMap());
                var totalVotingCount = votingCountMap.values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .sum();
                return new PollSummary(
                    poll.id(),
                    poll.title(),
                    poll.creatorName(),
                    poll.responseType(),
                    poll.pollType(),
                    poll.endAt(),
                    poll.readCount(),
                    totalVotingCount,
                    poll.items()
                        .stream()
                        .map(item -> new PollSummary.PollItem(
                            item.id(),
                            item.description(),
                            item.imageUrl(),
                            votingCountMap.getOrDefault(item.id(), 0)
                        ))
                        .toList()
                );
            })
            .toList();
    }

    @Override
    public Map<Long, Map<Long, Integer>> findPollItemIdsAndVoteCounts(List<Long> ids) {
        var polls = queryFactory
            .selectFrom(poll)
            .where(poll.id.in(ids))
            .fetch();

        if (polls.isEmpty()) {
            throw new BadRequestException(Status.POLL_NOT_FOUND);
        }

        var pollItemIds = polls.stream()
            .flatMap(poll -> poll.items().stream())
            .map(PollItem::id)
            .distinct()
            .toList();

        var votingCountByItems = queryFactory
            .select(votingItem.pollItemId, votingItem.count())
            .from(votingItem)
            .where(votingItem.pollItemId.in(pollItemIds))
            .groupBy(votingItem.pollItemId)
            .fetch()
            .stream()
            .collect(toMap(
                tuple -> tuple.get(votingItem.pollItemId),
                tuple -> tuple.get(votingItem.count()).intValue()
            ));

        return combine(
            polls,
            pollItemIds,
            votingCountByItems
        );
    }

    public Map<Long, Map<Long, Integer>> combine(List<Poll> polls,
                                                 List<Long> pollItemIds,
                                                 Map<Long, Integer> votingCountByItems) {
        return polls.stream()
            .collect(toMap(
                Poll::id,
                poll -> poll.items().stream()
                    .filter(item -> pollItemIds.contains(item.id()))
                    .collect(toMap(
                        PollItem::id,
                        item -> votingCountByItems.getOrDefault(item.id(), 0)
                    ))
            ));
    }
}