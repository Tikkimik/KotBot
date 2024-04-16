package com.javarush.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.javarush.telegrambot.TelegramBotContent.*;

public class MyFirstTelegramBot extends MultiSessionTelegramBot {
    private static final String NAME = System.getenv("NAME");
    private static final String TOKEN = System.getenv("TOKEN");
    private List<String> buttonLocators;
    private List<Map<String, String>> dialogButtons;
    private List<String> stepPics;
    private List<String> stepTexts;


    public MyFirstTelegramBot() {
        super(NAME, TOKEN);
        initialize();
    }

    @Override
    public void onUpdateEventReceived(Update updateEvent) {
        Message message = updateEvent.getMessage();

        if (message != null && message.hasText()) {
            String messageText = message.getText().toLowerCase();
            if (messageText.startsWith("/")) {
                executeCommand(messageText);
            }
        }

        String data = updateEvent.getCallbackQuery().getData();
        if (data != null && data.endsWith("button_locator")) dataInterceptor(data);
    }

    private void initGame() {
        setUserGlory(0);
        sendPhotoMessageAsync(stepPics.get(0));
        sendTextMessageAsync(STEP_1_TEXT, Map.of("Взлом холодильника", buttonLocators.get(0)));
    }

    private void dataInterceptor(String data) {
        for (int i = 0; i < buttonLocators.size(); i++) {
            if (data.contains(buttonLocators.get(i))) {
                calculateGlory(i);

                if (i == 8) {
                    sendTextMessageAsync(stepTexts.get(i + 1), dialogButtons.get(i + 1));
                    sendImageMessageAsync("src/main/resources/images/final_pic.jpg");
                    return;
                }

                sendPhotoMessageAsync(stepPics.get(i + 1));
                sendTextMessageAsync(stepTexts.get(i + 1), dialogButtons.get(i + 1));
                break;
            }
        }
    }

    private void calculateGlory(int i) {
        int[] gloryValues = {20, 20, 30, 30, 40, 40, 50};
        addUserGlory((i >= 0 && i < gloryValues.length) ? gloryValues[i] : 0);
    }

    private void executeCommand(String messageText) {
        switch (messageText) {
            case "/start" -> initGame();
            case "/bye" -> sendTextMessageAsync("Asta la vista, baby!");
            case "/glory" -> sendTextMessageAsync(String.format("Сейчас у тебя %s славы!", getUserGlory()));
            case "/help" -> sendTextMessageAsync("В данный момент поддерживаются команды:\n" +
                    " /start - стартовая команда\n" +
                    " /bye - прощальная команда\n" +
                    " /help - комманда с подсказками");
            default -> sendTextMessageAsync("*бибубип комманду не удалось распознать*");
        }
    }

    private void initialize() {
        buttonLocators = Arrays.asList(
                "step_1_button_locator",
                "step_2_button_locator",
                "step_3_button_locator",
                "step_4_button_locator",
                "step_5_button_locator",
                "step_6_button_locator",
                "step_7_button_locator",
                "step_8_button_locator",
                "step_9_button_locator"
        );

        stepPics = Arrays.asList(
                "step_1_pic",
                "step_2_pic",
                "step_3_pic",
                "step_4_pic",
                "step_5_pic",
                "step_6_pic",
                "step_7_pic",
                "step_8_pic",
                "final_pic"
        );

        stepTexts = Arrays.asList(
                STEP_1_TEXT,
                STEP_2_TEXT,
                STEP_3_TEXT,
                STEP_4_TEXT,
                STEP_5_TEXT,
                STEP_6_TEXT,
                STEP_7_TEXT,
                STEP_8_TEXT,
                FINAL_TEXT
        );

        dialogButtons = new ArrayList<>();
        dialogButtons.add(Map.of("Взлом холодильника", buttonLocators.get(0)));
        dialogButtons.add(Map.of(
                "Взять сосиску! +20 славы", buttonLocators.get(1),
                "Взять рыбу! +20 славы", buttonLocators.get(1),
                "Скинуть банку с огурцами! +20 славы", buttonLocators.get(1))
        );
        dialogButtons.add(Map.of("Взлом робота пылесоса", buttonLocators.get(2)));
        dialogButtons.add(Map.of(
                "Отправить робот пылесос за едой! +30 славы", buttonLocators.get(3),
                "Покататься на роботе пылесосе! +30 славы", buttonLocators.get(3),
                "Убежать от робота пылесоса! +30 славы", buttonLocators.get(3))
        );
        dialogButtons.add(Map.of("Надеть и включить GoPro!", buttonLocators.get(4)));
        dialogButtons.add(Map.of(
                "Бегать по крышам, снимать на GoPro! +40 славы", buttonLocators.get(5),
                "С GoPro нападать на других котов из засады! +40 славы", buttonLocators.get(5),
                "С GoPro нападать на собак из засады! +40 славы", buttonLocators.get(5))
        );
        dialogButtons.add(Map.of("Взлом пароля!", buttonLocators.get(6)));
        dialogButtons.add(Map.of("Выйти во двор \uD83D\uDC08", buttonLocators.get(7)));
        dialogButtons.add(Map.of("Спасибо за игру \uD83D\uDC08", buttonLocators.get(8)));
    }


    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new MyFirstTelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
