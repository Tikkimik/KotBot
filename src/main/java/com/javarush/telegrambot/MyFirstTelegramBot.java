package com.javarush.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

public class MyFirstTelegramBot extends MultiSessionTelegramBot {
    private static final String NAME = System.getenv("NAME");
    private static final String TOKEN = System.getenv("TOKEN");
    private static final String PET_CAT = "petCat";
    private static final String PET_DOG = "petDog";
    private Map<String, String> pets;

    public MyFirstTelegramBot() {
        super(NAME, TOKEN);
        initializePets();
    }

    private void initializePets() {
        pets = new HashMap<>();
        pets.put("Кот", PET_CAT);
        pets.put("Собака", PET_DOG);
    }

    @Override
    public void onUpdateEventReceived(Update updateEvent) {
        Message message = updateEvent.getMessage();

        if (message != null && message.hasText()) {
            String messageText = message.getText();
            if (isCommand(message.getText())) {
                executeCommand(messageText);
            } else {
                leaveLike();
            }
        }

        if (updateEvent.getCallbackQuery().getData() != null && updateEvent.getCallbackQuery().getData().contains("pet")) {
            handlePetSelection(updateEvent.getCallbackQuery().getData());
        }
    }

    private boolean isCommand(String messageText) {
        return messageText.startsWith("/");
    }

    private void choiceFavoritePet() {
        sendTextMessageAsync("Ваше любимое животное?", pets);
    }

    private void handlePetSelection(String selectedPet) {
        switch (selectedPet) {
            case PET_CAT -> sendPhotoMessageAsync("step_4_pic");
            case PET_DOG -> sendPhotoMessageAsync("step_6_pic");
        }
    }

    private void leaveLike() {
        sendTextMessageAsync("👍");
    }

    private void executeCommand(String messageText) {
        switch (messageText) {
            case "/start" -> sendTextMessageAsync("Добро пожаловать! \nВ данном боте можно поиграть!");
            case "/bye" -> sendTextMessageAsync("Asta la vista, baby!");
            case "/pet" -> choiceFavoritePet();
            case "/like" -> leaveLike();
            case "/help" -> sendTextMessageAsync("В данный момент поддерживаются 5 команды:\n" +
                    " /start - стартовая комманда\n" +
                    " /bye - прощальная комманда\n" +
                    " /pet - комманда выбора любиго питомца\n" +
                    " /like - leave a like\n" +
                    " /help - комманда с подсказками");
            default -> sendTextMessageAsync("*бибубип комманду не удалось распознать*");
        }
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
