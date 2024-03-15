package org.tg.gollaba.poll.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.service.GetPollListService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static org.tg.gollaba.common.support.QueryDslUtils.createColumnOrder;
import static org.tg.gollaba.poll.domain.QPoll.poll;

@RequiredArgsConstructor
public class PollRepositoryCustomImpl implements PollRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetPollListService.PollSummary> findPolls(GetPollListService.Requirement requirement) {
        var pageable = requirement.pageable();
        var where = Stream.of(
                requirement.optionGroup()
                    .map(optionGroup -> switch (optionGroup) {
                        case TITLE -> poll.title.contains(requirement.query().orElseThrow());
                    }),
                requirement.isActive()
                    .filter(Boolean.TRUE::equals)
                    .map(isEnded -> poll.endedAt.goe(now())),
                requirement.pollType()
                    .map(poll.pollType::eq)
            )
            .flatMap(Optional::stream)
            .toArray(Predicate[]::new);
        var orderBy = pageable.getSort()
            .stream()
            .map(order -> switch (order.getProperty()) {
                case "createdAt" -> createColumnOrder(poll.createdAt, order);
                case "endedAt" -> createColumnOrder(poll.endedAt, order);
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

        return new PageImpl<>(
            convert(polls),
            pageable,
            totalCount
        );
    }

    private List<GetPollListService.PollSummary> convert(List<Poll> polls) {
        return polls.stream()
            .map(poll -> new GetPollListService.PollSummary(
                poll.id(),
                poll.title(),
                poll.creatorName(),
                poll.responseType(),
                poll.pollType(),
                poll.endedAt(),
                poll.readCount(),
                poll.items()
                    .stream()
                    .map(item -> new GetPollListService.PollSummary.PollItem(
                        item.id(),
                        item.description(),
                        item.imageUrl(),
                        0 // TODO: 투표 수 조회 구현 할지 말지 정하기
                    ))
                    .toList()
            ))
            .toList();
    }
}
