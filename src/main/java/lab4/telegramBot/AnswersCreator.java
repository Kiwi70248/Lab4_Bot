package lab4.telegramBot;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class AnswersCreator {

    public static String getMessage(Message message, Client client) {
        switch (client.getStage()) {
            case WAIT :{
                return  ("Выберите команду для начала работы.");
            }
            case CREATE:{
                client.setStage(Stage.WRITE);
                return ("Для создания нового напоминания введите его время в формате \"yyyy.MM.dd HH:mm\" и его текст с новой строки. \nНапример:\n2019.12.21 12:00\nПоставить Кузнецовой зачет :).");
            }
            case WRITE:{
                String[] strings = message.getText().split("\n");
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                try {
                    Date date = format.parse (strings[0]);
                    String reminder = new String();
                    for (String string : strings) {
                        reminder = reminder + string + "\n";
                    }
                    client.putToReminders(date, reminder);
                    client.setStage(Stage.WAIT);
                    return "Напоминание успешно создано";
                } catch (ParseException e) {
                    e.printStackTrace();
                    return "Неправильный формат даты. Попробуйте ещё раз.";
                }
            }
            case WATCH:{
                if (!client.getReminders().isEmpty()) {
                    String answer = "Ваши текущие напоминания:\n\n";
                    for (String value : client.getReminders().values()) {
                        answer = answer + value + "\n";
                    }
                    client.setStage(Stage.WAIT);
                    return answer;
                } else {
                    client.setStage(Stage.WAIT);
                    return "У вас пока нет ни одного напоминания. Чтобы создать напоминание наберите \"Создать напоминание\"";
                }
            }
            case DELETE:{
                if (!client.getReminders().isEmpty()) {
                    String answer = "Ваши текущие напоминания:\n\n";
                    for (String value : client.getReminders().values()) {
                        answer = answer + value + "\n";
                    }
                    client.setStage(Stage.CONFIRM_DELETE);
                    return answer + "\nВведите дату для напоминания, которое хотите удалить или используйте \"Отмена\"";
                } else {
                    client.setStage(Stage.WAIT);
                    return "У вас пока нет напоминаний. Чтобы создать напоминание наберите \"Создать напоминание\"";
                }

            }
            case CONFIRM_DELETE:{
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");

                String dateString = format.format(new Date());
                try {
                    Date date = format.parse (message.getText());
                    if (client.hasDate(date)) {
                        client.getReminders().remove(date);
                        client.setStage(Stage.WAIT);
                        return "Ваше напоминание успешно удалено";
                    } else {
                        return "Напоминаний на данное время не найдено";
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return "Неправильный формат даты. Попробуйте ещё раз.";
                }
            }
            default:{
                return ("Ничего не понятно");
            }
        }
    }
}
