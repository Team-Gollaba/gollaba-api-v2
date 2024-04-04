package org.tg.gollaba.poll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tg.gollaba.common.exception.BadRequestException;
import org.tg.gollaba.poll.component.PollS3FileUploader;
import org.tg.gollaba.poll.domain.Poll;
import org.tg.gollaba.poll.domain.PollItem;
import org.tg.gollaba.poll.repository.PollRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.tg.gollaba.common.support.Status.POLL_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UpdatePollService {
    private final PollRepository pollRepository;
    private final PollS3FileUploader pollS3FileUploader;

    @Transactional
    public Long update(Requirement requirement) { //이미지가 1개 들어올 경우, 2개 들어올 경우, 아예 없는 경우
        var poll = pollRepository.findById(requirement.pollId())
            .orElseThrow(() -> new BadRequestException(POLL_NOT_FOUND));

        poll.update(
            requirement.title().orElse(null),
            requirement.endAt().orElse(null), //endAt이 null일 경우? => 여기서 Optional.empty()
            updatePollItems(
                poll,
                requirement
            )
        );

        pollRepository.save(poll); //TODO) 지금 저장에서 null이 되어서 예외가 터지는듯
        return poll.id();
    }

    private List<PollItem> updatePollItems(Poll poll, //TODO 이거 두 개를 한 메소드에 두는게 맞는 것인가? > 업데이트는 한 방에 하랬거늘
                                          Requirement requirement){
        //pollItems 변경값만 받으니까 poll에서 items의 아이디를 뽑나?
        var pollItems = poll.getItems(
            poll.items().stream()
                .map(PollItem::id)
                .collect(toList())
            );
        var pollItemsList = requirement.items();

        //request 받았을 때, pollItems가 전부 null이면 기존에 있는 pollItems를 반환 (TODO 이 부분이 컨트롤러의 빈 객체 반환이랑 이어지는거임)
            if(pollItemsList.size()==0){ //이걸 하지 않으면 밑에 스트림에 null이 들어가서 돌지 않음!
            return pollItems;
        } //왜 받은 pollItems가 0이지???????????

//TODO 4) for문(최종)
        for (int i = 0; i < pollItemsList.size(); i++) {
            var reqPollItem = pollItemsList.get(i);
            var pollItem = pollItems.get(i);

            // 이미지가 있는 경우
            reqPollItem.image().ifPresent(image -> {
                var imageUrl = pollS3FileUploader.uploadPollItemImage(
                    poll.id(),
                    pollItem.id(),
                    image
                );
                pollItem.changeImageUrl(imageUrl);
            });

            //null 넣어도 도메인에서 거를거라 if 조건 없어도 ㄱㅊ
            pollItem.changeDescription(pollItem.description());

            // 설명이 있는 경우 TODO null 해결시 아래 조건 지울거임: 도메인에 해당 걸러주기 있음ㅠㅠ 진짜아ㅠㅠㅠㅠ
//            reqPollItem.description().ifPresent(
//                pollItem::changeDescription
//            );
        }

        return pollItems;
    }

    public record Requirement(
        Long pollId,
        Optional<String> title,
        Optional<LocalDateTime> endAt,
        List<Requirement.Item> items
    ){
        public record Item(
            Optional<String> description,
            Optional<MultipartFile> image
        ){

        }
    }
}
