package org.tg.gollaba.participation.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.participation.application.ParticipateService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v2/participation")
@RequiredArgsConstructor
public class ParticipateController {
    private final ParticipateService service;

    @PostMapping
    public ApiResponse<Void> create(@RequestBody @Valid Request request,
                                    HttpServletRequest httpServletRequest) {
        var requirement = createRequirement(
            request,
            httpServletRequest
        );

        service.participate(requirement);

        return ApiResponse.success();
    }

    private ParticipateService.Requirement createRequirement(Request request,
                                                             HttpServletRequest httpServletRequest) {
        return new ParticipateService.Requirement(
            request.pollId(),
            request.pollItemIds(),
            "", // TODO: IP 주소 추가
            request.userId(),
            request.participantName()
        );
    }

    record Request(
        @NotNull(message = "투표 ID는 필수입니다.")
        Long pollId,
        @NotEmpty(message = "투표 항목 ID는 필수입니다.")
        Set<Long> pollItemIds,
        Optional<Long> userId,
        Optional<String> participantName
    ) {
    }
}
