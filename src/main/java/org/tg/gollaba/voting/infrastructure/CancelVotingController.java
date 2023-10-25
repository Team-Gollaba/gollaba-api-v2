package org.tg.gollaba.voting.infrastructure;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.voting.application.CancelVotingService;

@RestController
@RequestMapping("/v2/voting/{votingId}")
@RequiredArgsConstructor
public class CancelVotingController {
    private final CancelVotingService service;

    @DeleteMapping //TODO user 인증 추가
    public ApiResponse<Void> cancel(@PathVariable
                                    @NotNull(message = "비회원일 경우, 투표를 취소할 수 없습니다.")
                                    Long votingId){
        service.cancel(votingId);

        return ApiResponse.success();
    }
}
