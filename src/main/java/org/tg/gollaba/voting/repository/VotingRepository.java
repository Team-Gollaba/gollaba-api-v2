package org.tg.gollaba.voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tg.gollaba.voting.domain.Voting;

import java.util.Optional;

public interface VotingRepository extends JpaRepository<Voting, Long>, VotingRepositoryCustom {

    @Query("""
        SELECT CASE WHEN COUNT(v) > 0
            THEN TRUE
            ELSE FALSE
            END
        FROM Voting v
        WHERE v.pollId = :pollId
        AND v.userId = :userId
        AND v.deletedAt is null
    """)
    boolean existsActiveVotingBy(Long pollId, Long userId);

    @Query("""
        SELECT v FROM Voting v
        WHERE v.pollId = :pollId
        AND v.userId = :userId
        AND v.deletedAt is null
    """)
    Optional<Voting> findActiveVotingBy(Long pollId, Long userId);
}
