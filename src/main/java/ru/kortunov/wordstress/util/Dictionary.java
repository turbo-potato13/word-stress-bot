package ru.kortunov.wordstress.util;

import java.util.Optional;

public interface Dictionary {
    String read();

    Optional<String> search(String searchWord);

    String prepareMessage(String message);
}
