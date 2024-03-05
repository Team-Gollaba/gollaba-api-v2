package org.tg.gollaba.voting.component;

public interface DuplicatedVotingChecker {

    void check(String ipAddress, Long pollId);

    void record(String ipAddress, Long pollId);

}