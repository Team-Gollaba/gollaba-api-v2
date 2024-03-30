package org.tg.gollaba.common.exception;

public interface ErrorNotificationSender {

    void send(String message, Throwable e);

    void send(Throwable e);

    void send(String message);
}
