package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;
import org.tg.gollaba.user.repository.UserRepository;
import org.tg.gollaba.voting.repository.VotingRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPollListService {
    private final PollRepository pollRepository;
    private final VotingRepository votingRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PollSummary> get(Requirement requirement) {
        var polls = pollRepository.findPollList(requirement);
        var pollIds = polls.stream()
            .map(Poll::id)
            .toList();

        var votingCountMapByPollId = votingRepository.votingCountMapByPollId(pollIds);
        var pollIdUserProfileUrlMap = getPollIdToUserProfileMap(pollIds, pollRepository, userRepository);

        return PollSummary.convertToPage(
            polls,
            pollIdUserProfileUrlMap,
            votingCountMapByPollId,
            requirement.pageable());
        }

    private static Map<Long, String> getPollIdToUserProfileMap(List<Long> pollIds,
                                                               PollRepository pollRepository,
                                                               UserRepository userRepository) {
        // (1) userProfile pollId:userId //userId==null일떄, 기본값 -1L 할당
        var pollUserMap = pollRepository.findUserIdsByPollIds(pollIds);

        // (2) userIdsList
        var userIds = pollUserMap.values().stream().collect(Collectors.toList());

        //(3) userId:UserProfileMap // userId==-1L 이면 userProfile 기본값"" 할당
        var userProfileMap = userRepository.findProfileImagesByUserIds(userIds);

        // (4) pollId:ProfileImageUrl // userId가 -1L인 경우 profileImageUrl > ""(null)로 처리
        return pollUserMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> userProfileMap.getOrDefault(entry.getValue(), "") // userId가 -1L >> value==""
            ));
    }

    public record Requirement(
        Optional<OptionGroup> optionGroup,
        Optional<String> query,
        Optional<Boolean> isActive,
        Optional<Poll.PollType> pollType,
        Pageable pageable
    ) {
    }

    public enum OptionGroup {
        TITLE
    }
}
