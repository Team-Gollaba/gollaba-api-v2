package org.tg.gollaba.voting.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tg.gollaba.common.FixtureReflectionUtils;
import org.tg.gollaba.common.TestFixture;

@Getter
@Setter
@Accessors(chain = true)
public class VoterNameFixture implements TestFixture<VoterName> {
    private String value = "testName";

    @Override
    public VoterName build() {
        var voterName = new VoterName();
        FixtureReflectionUtils.reflect(voterName, this);
        return voterName;
    }
}
