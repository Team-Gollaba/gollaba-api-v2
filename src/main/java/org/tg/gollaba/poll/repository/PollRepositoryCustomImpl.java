package org.tg.gollaba.poll.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollListService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static org.tg.gollaba.common.support.QueryDslUtils.createColumnOrder;
import static org.tg.gollaba.poll.domain.QPoll.poll;

@RequiredArgsConstructor
public class PollRepositoryCustomImpl implements PollRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Poll> findPollList(GetPollListService.Requirement requirement) {
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

        var pollEntities = queryFactory
            .selectFrom(poll)
            .where(where)
            .orderBy(orderBy)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(pollEntities, pageable, totalCount);
    }

    @Override
    public Page<Poll> findMyPolls(Long userId, Pageable pageable) {
        var totalCount = queryFactory
            .select(poll.countDistinct())
            .from(poll)
            .where(poll.userId.eq(userId))
            .fetchOne();

        if (totalCount == null || totalCount == 0) {
            return Page.empty();
        }

        var pollEntities = queryFactory
            .selectFrom(poll)
            .where(poll.userId.eq(userId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(pollEntities, pageable, totalCount);
    }

    @Override
    public Map<Long, Long> findUserIdsByPollIds(List<Long> pollIds) {
        if (pollIds.isEmpty()) {
            return Map.of();
        }

        return queryFactory
            .select(poll.id, poll.userId)
            .from(poll)
            .where(poll.id.in(pollIds))
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(poll.id),
                tuple -> tuple.get(poll.userId) == null ? -1L : tuple.get(poll.userId) // userId가 null이면 -1L로 처리
            ));
    }
}