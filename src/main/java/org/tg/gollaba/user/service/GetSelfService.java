package org.tg.gollaba.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.user.domain.User;
import org.tg.gollaba.user.repository.UserRepository;

import static org.tg.gollaba.common.support.Status.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GetSelfService {
    private final UserRepository userRepository;

    @Transactional
    public UserDetails get(Long userId){
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));

        return new UserDetails(
            user.name(),
            user.email(),
            user.roleType(),
            user.providerType(),
            user.profileImageUrl(),
            user.backgroundImageUrl()
        );
    }

    public record UserDetails(
        String name,
        String email,
        User.RoleType roleType,
        User.ProviderType providerType,
        String profileImageUrl,
        String backgroundImageUrl
    ){
    }
}

