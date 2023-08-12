package org.tg.gollaba.service;

import org.tg.gollaba.domain.Poll;
import org.tg.gollaba.domain.PollOption;
import org.tg.gollaba.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tg.gollaba.dto.PollDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;

    @Transactional // TODO: 구현하기
    public PollDto create(CreateRequirement requirement){

        var poll = requirement.toEntity();

        var pollOptions = PollOptionRequirement
                .createPollOptions(requirement, poll);
        addPollOptions(poll, pollOptions);


        pollRepository.save(poll);
        return PollDto.from(poll);
    }

    public void addPollOptions(Poll poll, List<PollOption> pollOptions) {
        for (PollOption pollOption : pollOptions) {
            poll.getOptions().add(pollOption);
        }
    }

    public record CreateRequirement(
            Optional<Long> userId, //creatorId리
            String title,
            String creatorName,
            Poll.PollType pollType,
            Poll.PollResponseType responseType,
            List<PollOptionRequirement> pollOptions


    ) {
        public  Poll toEntity() {
            return new Poll(
                userId.orElse(null),//userId,
                title,
                creatorName,
                pollType,
                responseType
            );
        }
    }

    //이거 엔티티 만들겠다고 PollOption 생성자 추가하면 바로 몽둥이겠지???
    public record PollOptionRequirement( //얘는 poll 만들면서 만들어질거기 때문에 ... 따로 생성자 만들 필요 없음
            String description,
            String imageUrl
    ){
        public PollOption toEntity(Poll poll){
            return new PollOption(
                    poll,
                    description,
                    imageUrl
            );
        }

        //requirement에서 pollOption만 빼야함, 그리고 그것만 엔티티로 만들어서 리스트로 반환해야함
        public static List<PollOption> createPollOptions(CreateRequirement requirement, Poll poll) {
            return requirement.pollOptions().stream()
                    .map(optionRequirement -> optionRequirement.toEntity(poll))
                    .toList();
        }
    }
}