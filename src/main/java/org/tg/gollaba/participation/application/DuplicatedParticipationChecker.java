package org.tg.gollaba.participation.application;

public interface DuplicatedParticipationChecker {

    void check(String ipAddress, Long pollId);

    void record(String ipAddress, Long pollId);

}