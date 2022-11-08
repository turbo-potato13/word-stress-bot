package ru.kortunov.wordstress.dto;

public enum TelegramCommand {
    START("/start"),
    HELP("/help"),
    ORFO_EGE("Словарь ударений для ЕГЭ"),
    ORFO_ALL("Полный словаь ударений (beta)");

    private final String value;

    TelegramCommand(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TelegramCommand fromValue(String value) {
        for (TelegramCommand tc : TelegramCommand.values()) {
            if (tc.value.equals(value))
                return tc;
        }
        return null;
    }
}
