package org.tg.gollaba.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.tg.gollaba.user.domain.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override//userId:profileImageUrl
    public Map<Long, String> findProfileImagesByUserIds(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return queryFactory
            .select(user.id, user.profileImageUrl)
            .from(user)
            .where(user.id.in(userIds))
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(user.id),
                tuple -> {
                    var userId = tuple.get(user.id);
                    // userId가 -1L인 경우 profileImageUrl을 null로 처리
                    return userId == -1L ? "" : tuple.get(user.profileImageUrl); // userId가 -1L이면 "" 반환
                }
            ));
    }

}
