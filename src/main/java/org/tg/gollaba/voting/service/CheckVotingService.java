package org.tg.gollaba.voting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.voting.component.DuplicatedVotingChecker;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckVotingService {
    private final VotingRepository votingRepository;
    private final DuplicatedVotingChecker duplicatedVotingChecker;

    public boolean check(Requirement requirement) {
        if (existsByIpAddress(requirement.ipAddress(), requirement.pollId())) {
            return true;
        }

        if (existsByUserId(requirement.userId(), requirement.pollId())) {
            return true;
        }

        return false;
    }

    private boolean existsByIpAddress(String ipAddress, Long pollId) {
        try {
            duplicatedVotingChecker.check(ipAddress, pollId);
        } catch(BadRequestException e) {
            return true;
        }

        return false;
    }

    private boolean existsByUserId(Optional<Long> userId, Long pollId) {
        return userId
            .map(id -> votingRepository.existsByPollIdAndUserId(pollId, id))
            .orElse(false);
    }


    public record Requirement(
        Long pollId,
        String ipAddress,
        Optional<Long> userId
    ) {
    }
}
