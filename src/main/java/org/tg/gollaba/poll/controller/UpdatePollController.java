package org.tg.gollaba.poll.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.auth.vo.AuthenticatedUser;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.common.support.Status;
import org.tg.gollaba.common.web.ApiResponse;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.poll.controller.UpdatePollController.Request.Item;
import org.tg.gollaba.poll.service.UpdatePollService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.tg.gollaba.poll.controller.UpdatePollController.Request.Item.to;

//TODO Invalid property 'items[0]' of bean class [org.tg.gollaba.poll.controller.UpdatePollController$Request]: Illegal attempt to get property 'items' threw exception

@RestController
@RequestMapping("/v2/polls/{pollHashId}/update")
@RequiredArgsConstructor
public class UpdatePollController {
    private final HashIdHandler hashIdHandler;
    private final UpdatePollService service;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping //유저 사용한다는 그 ..... 어노테이션이 잇지 않나?
    public ApiResponse<Void> update(AuthenticatedUser user,
                                    @PathVariable String pollHashId,
                                    @Valid Request request){
        isEmptyPayload(request);
        service.update(
            to(
                request,
                hashIdHandler,
                pollHashId
            )
        );

        return ApiResponse.success(null);
    }

    private void isEmptyPayload(Request request) {
        if (request.title.isEmpty() &&
            (request.items == null || request.items.isEmpty()) && // items가 null 또는 비어 있는 경우 체크 추가
            request.endAt.isEmpty()) {
            throw new BadRequestException(Status.INVALID_PARAMETER);
        }
        //TODO Invalid property 'items[0]' of bean class [org.tg.gollaba.poll.controller.UpdatePollController$Request]: Illegal attempt to get property 'items' threw exception
    }

    public record Request( //TODO 전부 다 변경 가능이라, Optional을 붙여야 하는데 그 처리!!
        Optional<String> title,
        Optional<LocalDateTime> endAt,
        List<Request.Item> items
    ){
        public record Item(
            Optional<String> description,
            Optional<MultipartFile> image
        ){
            public static UpdatePollService.Requirement to(Request request,
                                                           HashIdHandler hashIdHandler,
                                                           String pollHashId){

                List<UpdatePollService.Requirement.Item> requirementPollItems;
                //TODO ㄴ> pollItems는 전부 null이 될 수도 있음 -> poll에 관한 것만 변경하는 경우
                //그 경우에 빈 값을 전달해서, 도메인에서 아무런 변경도 하지 않을 수 있도록 해야함
                //ㄴ> 그거 때문에 여기서 얘만 선언하기 (빈 값을 넣는 경우에는 빈 값만 넘겨야 하니까)

                if (request.items == null || request.items.stream().allMatch(Item::isEmpty)){
                    requirementPollItems = Collections.emptyList();
                } else {
                    requirementPollItems = request.items.stream()
                        .map(pollItem -> new UpdatePollService.Requirement.Item(
                            pollItem.description,
                            pollItem.image
                        ))
                        .collect(toList());
                }

                return new UpdatePollService.Requirement(
                    hashIdHandler.decode(pollHashId),
                    request.title,
                    request.endAt,
                    requirementPollItems
                );
            }

            public boolean isEmpty() {
                return image.isEmpty() && description.isEmpty();
            }

    }
}}
