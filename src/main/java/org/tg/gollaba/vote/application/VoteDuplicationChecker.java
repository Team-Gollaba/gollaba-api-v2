package org.tg.gollaba.vote.application;

public interface VoteDuplicationChecker {

    void check(String ipAddress, Long pollId);

    void save(String ipAddress, Long pollId);

}