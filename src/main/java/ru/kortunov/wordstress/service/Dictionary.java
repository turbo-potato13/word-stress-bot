package ru.kortunov.wordstress.service;

import ru.kortunov.wordstress.dto.TelegramCommand;

import java.util.Optional;

public interface Dictionary {

    Optional<String> search(String searchWord, TelegramCommand command);

    String prepareMessage(String message);
}
