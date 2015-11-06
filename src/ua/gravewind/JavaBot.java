package ua.gravewind;

/**
 * Бот, относящийся к одной учетной записи жабера. Реализует интерфейс Runnable,
 * так что разных ботов можно будет запускать в разных потоках, или
 * комбинировать их.<hr>
 *
 * Использует библиотеку smack.jar и smackx.jar: org.jivesoftware.smack<hr>
 *
 * @author V2
 *
 */
import ua.gravewind.EngineBot;

/*
 * Leks13 GPL v3
 */
public class JavaBot {

    public static void main(String[] args) {
        String botNick = "Bot";
        String botPassword = "itispass";
        String botDomain = "jabber.ru";
        String botServer = "jabber.ru";
        String botConfNick = "BotNickName";
        String NameMaster = "Master@jabber.ru";
        String confName = "example@conference.jabber.ru";
        String status = "Вот бот, и вот его статус";
        int botPort = 5222;

        EngineBot bot = new EngineBot(botNick, botPassword, botDomain,
                botServer, botPort, botConfNick, confName, NameMaster, status);
        Thread botThread = new Thread(bot);
        try {

            while (true) {
                botThread.start();
                Thread.sleep(3000);
                System.out.println("Started");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }
}

