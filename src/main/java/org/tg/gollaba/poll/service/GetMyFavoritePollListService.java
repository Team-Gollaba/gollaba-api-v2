package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.poll.repository.PollRepository;
import org.tg.gollaba.poll.vo.PollSummary;
import org.tg.gollaba.user.repository.UserRepository;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class GetMyFavoritePollListService {
    private final UserRepository userRepository;
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public Page<PollSummary> get(Long userId, Pageable pageable){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(Status.USER_NOT_FOUND));

        return pollRepository.findMyFavoritePolls(user.id(), pageable);
    }
}
