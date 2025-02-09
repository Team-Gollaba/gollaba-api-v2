package org.tg.gollaba.report.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.support.IpAddressExtractor;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.report.domain.PollReport;
import org.tg.gollaba.report.service.ReportService;

@RestController
@RequestMapping("/v2/polls/{pollHashId}/reports")
@RequiredArgsConstructor
public class ReportController {
    private final HashIdHandler hashIdHandler;
    private final ReportService service;

    @PostMapping(headers = HttpHeaders.AUTHORIZATION)
    public ApiResponse<Void> report(AuthenticatedUser user,
                                    @NotBlank(message = "pollHashId 는 필수값입니다.")
                                    @PathVariable String pollHashId,
                                    @RequestBody Request request) {
        var pollId = hashIdHandler.decode(pollHashId);
        service.report(
            new ReportService.Requirement(
                pollId,
                user.id().toString(),
                PollReport.ReporterType.REGISTERED_USER,
                request.reportType(),
                request.content()
            )
        );
        return ApiResponse.success();
    }

    @PostMapping
    public ApiResponse<Void> report(HttpServletRequest httpServletRequest,
                                    @NotBlank(message = "pollHashId 는 필수값입니다.")
                                    @PathVariable String pollHashId,
                                    @RequestBody Request request) {
        var pollId = hashIdHandler.decode(pollHashId);
        service.report(
            new ReportService.Requirement(
                pollId,
                IpAddressExtractor.extract(httpServletRequest),
                PollReport.ReporterType.UNREGISTERED_USER,
                request.reportType(),
                request.content()
            ));
        return ApiResponse.success();
    }

    record Request(
        @NotBlank(message = "신고 내용이 존재해야 합니다.")
        String content,
        @NotNull(message = "신고 유형을 선택해주세요.")
        PollReport.ReportType reportType
    ){}
}

