package org.tg.gollaba.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.user.repository.UserRepository;

import static org.tg.gollaba.common.support.Status.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GetSelfService {
    private final UserRepository userRepository;

    @Transactional
    public Requirement get(Long userId){

        var user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));

        return new Requirement(
            user.name(),
            user.email(),
            user.roleType().toString(),
            user.providerType() != null ?
                user.providerType().toString() : null,
            user.profileImageUrl(),
            user.backgroundImageUrl()
        );
    }

    public record Requirement(
        String name,
        String email,
        String roleType,
        String providerType,
        String profileImageUrl,
        String backgroundImageUrl
    ){}
}

