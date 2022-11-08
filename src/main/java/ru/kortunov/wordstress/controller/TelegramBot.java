package ru.kortunov.wordstress.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kortunov.wordstress.dto.TelegramCommand;
import ru.kortunov.wordstress.service.OrfoDictionary;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private static final String HELP_MESSAGE = "В данном боте вы можете проверить ударения в словах. " +
            "Для этого выберите словарь и отправьте интересующее вас слово. " +
            "Полный словарь ударений пока не отредактирован, поэтому возможны лишние результаты слов, но ударения в словах раставлены верно" ;
    private static final String START_MESSAGE = "Привет! Этот бот поможет тебе с проверкой ударения. Выбери нужный словарь. Для помощи воспользуйся /help";

    private TelegramCommand telegramCommand = TelegramCommand.START;
    private final OrfoDictionary orfoDictionary;
    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    @PostConstruct
    private void init() {
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Начать работу бота"));
        listofCommands.add(new BotCommand("/help", "Информация как пользоваться ботом"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = update.getMessage();
        var messageText = message.getText();
        log.info(messageText);

        var command = TelegramCommand.fromValue(messageText);
        if (command != null) {
            telegramCommand = command;
            if (telegramCommand.equals(TelegramCommand.HELP) || telegramCommand.equals(TelegramCommand.START)) {
                String result = command.equals(TelegramCommand.START) ? START_MESSAGE : HELP_MESSAGE;
                sendMessage(message, result);
            }
            return;
        }

        if (telegramCommand.equals(TelegramCommand.ORFO_EGE) || telegramCommand.equals(TelegramCommand.ORFO_ALL)) {
            var result = orfoDictionary.search(orfoDictionary.prepareMessage(message.getText()), telegramCommand);
            sendMessage(message, result.orElse("Слово не найдено"));
        } else {
            sendMessage(message, "Выберите словарь");
        }
    }

    public void sendMessage(Message message, String text) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);

        callKeyBoard(sendMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void callKeyBoard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add(TelegramCommand.ORFO_EGE.getValue());
        row.add(TelegramCommand.ORFO_ALL.getValue());

        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);
    }


}
