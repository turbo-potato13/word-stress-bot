package ru.kortunov.wordstress.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kortunov.wordstress.dto.TelegramCommand;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrfoDictionary implements Dictionary {

    private final HashSet<String> parsedTextOrfoEGE = new HashSet<>();
    private final HashSet<String> parsedTextOrfoAll = new HashSet<>();
    @Value("${orfo.dictionary.ege.path}")
    private String orfoEgePath;
    @Value("${orfo.dictionary.all.path}")
    private String orfoAllPath;

    @PostConstruct
    public void init() {
        try {
            parsedTextOrfoAll.addAll(Files.readAllLines(Paths.get(orfoAllPath)));
            parsedTextOrfoEGE.addAll(Files.readAllLines(Paths.get(orfoEgePath)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Optional<String> search(String searchWord, TelegramCommand command) {
        StringBuilder result = new StringBuilder();

        if (command.equals(TelegramCommand.ORFO_EGE)) {
            parsedTextOrfoEGE.stream()
                    .map(String::trim)
                    .filter(s -> s.toLowerCase().contains(searchWord))
                    .forEach(s -> result.append(s).append("\n\n"));
        } else if (command.equals(TelegramCommand.ORFO_ALL)) {
            parsedTextOrfoAll.stream()
                    .map(String::trim)
                    .filter(s -> s.toLowerCase().contains(searchWord))
                    .forEach(s -> result.append(s).append("\n\n"));
        }
        return result.toString().isEmpty() ? Optional.empty() : Optional.of(result.toString());
    }

    @Override
    public String prepareMessage(String message) {
        return message.trim().toLowerCase();
    }

}
