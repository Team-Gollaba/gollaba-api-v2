package org.tg.gollaba.poll.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.entity.BaseEntity;
import org.tg.gollaba.common.support.ValidationUtils;

@Entity
@Getter
@Accessors(fluent = true)
@NoArgsConstructor
public class PollItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column
    private String imageUrl;

    public PollItem(String description) {
        this.description = description;
    }

    public void changeImageUrl(String imageUrl) {
        if (!ValidationUtils.isValidUrl(imageUrl)) {
            throw new IllegalArgumentException("[PollItem][changeImageUrl] 유효하지 않은 URL 입니다.");
        }

        this.imageUrl = imageUrl;
    }
}
