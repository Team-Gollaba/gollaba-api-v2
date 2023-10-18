package org.tg.gollaba.participation.infrastructure;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.participation.application.CancelParticipationService;

@RestController
@RequestMapping("/v2/participation")
@RequiredArgsConstructor
public class CancelParticipationController {
    private final CancelParticipationService service;

    @DeleteMapping("/{participantId}") //TODO user 추가
    public ApiResponse<Void> cancel(@PathVariable
                                        @NotNull(message = "비회원일 경우, 투표를 취소할 수 없습니다.")
                                        Long participantId){
        service.cancel(participantId);

        return ApiResponse.success();
    }
}
