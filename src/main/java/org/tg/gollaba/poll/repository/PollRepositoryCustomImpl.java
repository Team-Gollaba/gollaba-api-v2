package org.tg.gollaba.poll.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.service.GetPollDetailsService;
import org.tg.gollaba.poll.service.GetPollListService;
import org.tg.gollaba.poll.vo.PollSummary;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.tg.gollaba.common.support.QueryDslUtils.createColumnOrder;
import static org.tg.gollaba.favorites.domain.QFavorites.favorites;
import static org.tg.gollaba.poll.domain.QPoll.poll;
import static org.tg.gollaba.stats.domain.QPollDailyStats.pollDailyStats;
import static org.tg.gollaba.stats.domain.QPollStats.pollStats;
import static org.tg.gollaba.user.domain.QUser.user;
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
                    .map(isActive -> isActive
                        ? poll.endAt.goe(now())
                        : poll.endAt.loe(now())
                    ),
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
        var pollCreatorProfileMap = getUserProfileImageUrls(polls);

        return new PageImpl<>(
            convert(polls, pollCreatorProfileMap, votingCountMapByPollId),
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
        var creatorProfileUrl = getCreatorProfileUrl(pollEntity);

        return new GetPollDetailsService.PollDetails(
            pollEntity.id(),
            pollEntity.title(),
            pollEntity.creatorName(),
            creatorProfileUrl,
            pollEntity.responseType(),
            pollEntity.pollType(),
            pollEntity.endAt(),
            totalVotingCount,
            pollEntity.readCount(),
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
            .where(
                voting.pollId.in(pollIds),
                voting.deletedAt.isNull()
            )
            .groupBy(voting.pollId, votingItem.pollItemId)
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
                                      Map<Long, String> pollCreatorProfileMap,
                                      Map<Long, Map<Long, Integer>> votingCountMapByPollId) {
        return polls.stream()
            .map(poll -> {
                var votingCountMap = votingCountMapByPollId.getOrDefault(poll.id(), emptyMap());
                var totalVotingCount = votingCountMap.values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .sum();
                var creatorProfileUrl = pollCreatorProfileMap.get(poll.id());
                if ("".equals(creatorProfileUrl)) {
                    creatorProfileUrl = null;  // "" >> null 변환
                }
                return new PollSummary(
                    poll.id(),
                    poll.title(),
                    poll.creatorName(),
                    creatorProfileUrl,
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
    public Page<PollSummary> findMyPolls(Long userId, Pageable pageable) {
        var totalCount = queryFactory
            .select(poll.countDistinct())
            .from(poll)
            .where(poll.userId.eq(userId))
            .fetchOne();

        if (totalCount == null || totalCount == 0) {
            return Page.empty();
        }

        var polls = queryFactory
            .selectFrom(poll)
            .where(poll.userId.eq(userId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

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

        var pollCreatorProfileMap = getUserProfileImageUrls(polls);

        var response = combine(
            polls,
            votingCountByItems
        );

        return new PageImpl<>(
            convert(polls, pollCreatorProfileMap, response),
            pageable,
            totalCount
        );
    }

    private Map<Long, Map<Long, Integer>> combine(List<Poll> polls,
                                                  Map<Long, Integer> votingCountByItems) {
        return polls.stream()
            .collect(toMap(
                Poll::id,
                poll -> poll.items().stream()
                    .collect(toMap(
                        PollItem::id,
                        item -> votingCountByItems.getOrDefault(item.id(), 0)
                    ))
            ));
    }

    @Override
    public List<PollSummary> findTopPolls(LocalDate aggregationDate, int limit) {
        var polls = queryFactory
            .selectFrom(poll)
            .join(pollStats).on(pollStats.pollId.eq(poll.id))
            .where(pollStats.aggregationDate.eq(aggregationDate))
            .orderBy(
                pollStats.totalVoteCount.desc(),
                pollStats.totalReadCount.desc(),
                pollStats.totalFavoritesCount.desc(),
                pollStats.pollId.desc()
            )
            .limit(limit)
            .fetch();

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
            .collect(Collectors.toMap(
                tuple -> tuple.get(votingItem.pollItemId),
                tuple -> tuple.get(votingItem.count()).intValue()
            ));

        var pollCreatorProfileMap = getUserProfileImageUrls(polls);

        var response = combine(
            polls,
            votingCountByItems
        );

        return convert(polls, pollCreatorProfileMap, response);
    }

    public List<PollSummary> findTrendingPolls(LocalDate aggregationDate, int limit){
        var polls = queryFactory
            .selectFrom(poll)
            .innerJoin(pollDailyStats)
            .on(poll.id.eq(pollDailyStats.pollId))
            .where(pollDailyStats.aggregationDate.eq(aggregationDate))
            .orderBy(
                pollDailyStats.voteCount.desc(),
                pollDailyStats.readCount.desc(),
                pollDailyStats.favoritesCount.desc(),
                poll.id.desc())
            .limit(limit)
            .fetch();

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
            .collect(Collectors.toMap(
                tuple -> tuple.get(votingItem.pollItemId),
                tuple -> tuple.get(votingItem.count()).intValue()
            ));

        var pollCreatorProfileMap = getUserProfileImageUrls(polls);

        var response = combine(
            polls,
            votingCountByItems
        );

        return convert(polls, pollCreatorProfileMap, response);
    }

    @Override
    public Page<PollSummary> findMyVotingPolls(Long userId, Pageable pageable){
        var pollIds = queryFactory
            .select(voting.pollId)
            .from(voting)
            .where(voting.userId.eq(userId))
            .fetch();

        var totalCount = pollIds.size();

        if (totalCount == 0) {
            return Page.empty();
        }

        var polls = queryFactory
            .selectFrom(poll)
            .where(poll.id.in(pollIds))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

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

        var pollCreatorProfileMap = getUserProfileImageUrls(polls);

        var response = combine(
            polls,
            votingCountByItems
        );

        return new PageImpl<>(
            convert(polls, pollCreatorProfileMap, response),
            pageable,
            totalCount
        );
    }

    public String getCreatorProfileUrl(Poll pollEntity) {
        return queryFactory
            .select(user.profileImageUrl)
            .from(poll)
            .join(user).on(poll.userId.eq(user.id))
            .where(poll.id.eq(pollEntity.id()))
            .fetchOne();
    }

    private Map<Long, String> getUserProfileImageUrls(List<Poll> polls) {
        var pollIds = polls.stream()
            .map(Poll::id)
            .collect(Collectors.toList());

        var pollList = queryFactory
            .select(poll.id, user.profileImageUrl)
            .from(poll)
            .join(user).on(poll.userId.eq(user.id))
            .where(poll.id.in(pollIds))
            .fetch();

        var pollToProfileImageUrlMap = pollList.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(poll.id),
                tuple -> tuple.get(user.profileImageUrl) != null ? tuple.get(user.profileImageUrl) : ""
            ));

        return polls.stream()
            .collect(Collectors.toMap(
                Poll::id,
                pollEntity -> {
                    if (pollEntity.userId() == null || pollEntity.userId().equals(-1L)) {
                        return "";
                    }

                    return pollToProfileImageUrlMap.getOrDefault(pollEntity.id(), "");
                }
            ));
    }

    @Override
    public Page<PollSummary> findMyFavoritePolls(Long userId, Pageable pageable) {
        var totalCount = queryFactory
            .select(poll.countDistinct())
            .from(favorites)
            .join(poll).on(favorites.pollId.eq(poll.id))
            .where(favorites.userId.eq(userId))
            .fetchOne();

        if (totalCount == null || totalCount == 0) {
            return Page.empty();
        }

        var polls = queryFactory
            .selectFrom(poll)
            .join(favorites).on(favorites.pollId.eq(poll.id))
            .where(favorites.userId.eq(userId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

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

        var pollCreatorProfileMap = getUserProfileImageUrls(polls);

        var response = combine(
            polls,
            votingCountByItems
        );

        return new PageImpl<>(
            convert(polls, pollCreatorProfileMap, response),
            pageable,
            totalCount
        );
    }
}