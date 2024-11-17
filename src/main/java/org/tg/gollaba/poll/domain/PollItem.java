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
        this.imageUrl = imageUrl;
    }

    public void changeDescription(String description){
        assert description != null;

        this.description = description;
    }
}
