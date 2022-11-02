package ru.kortunov.wordstress.service;

import java.util.Optional;

public interface Dictionary {
    String read();

    Optional<String> search(String searchWord);

    String prepareMessage(String message);
}
