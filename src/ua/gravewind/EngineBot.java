/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.gravewind;

import java.util.StringTokenizer;
import javax.net.ssl.SSLException;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 *
 * @author Gravewind
 * @version 0.7.1
 */


public class EngineBot implements Runnable {

    private final String nick;
    private final String password;
    private String domain;
    private String server;
    private int port;
    private String nickInConf;
    private String confName;
    private String Master;
    private String status;
    private ConnectionConfiguration connConfig;
    private XMPPConnection connection;
    private MultiUserChat mch=null;
    private Do doit;
    String[] massUser = null;
    int[] resUser = null;
    int k = 0;
    

    /**
     * В конструктор должны передаваться данные, необходимые для авторизации на
     * жабер-сервере
     *
     * @param nick ник
     * @param password пароль
     * @param domain домен
     * @param server сервер
     * @param port порт
     * @param NickInConf ник в конференции
     * @param ConfName название конференции
     * @param Master JID хозяина бота
     * @param Status статус бота
     */
    public EngineBot(String nick, String password, String domain, String server,
            int port, String NickInConf, String ConfName, String Master, String status) {
        this.nick = nick;
        this.password = password;
        this.domain = domain;
        this.server = server;
        this.port = port;
        this.nickInConf = NickInConf;
        this.confName = ConfName;
        this.Master = Master;
        this.status = status;
        doit = new ua.gravewind.Do();
    }
    public PacketListener myListener = new PacketListener() {

                @Override
                public void processPacket(Packet packet) {
                    if (packet instanceof Message) {
                        Message message = (Message) packet;
                        // обработка входящего сообщения
                        processMessage(message);
                    }
                }
    };

    @Override
    public void run() {
        connConfig = new ConnectionConfiguration(server, port, domain);
        connection = new XMPPConnection(connConfig);
        connConfig.setReconnectionAllowed(true);
       while (true){
           try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {
                System.out.println(ex.getCause());
            }
        try {
            int priority = 2;
            //Немного магии
            DiscussionHistory history = new DiscussionHistory();
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            connection.connect();
            connection.login(nick, password);
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus(status);
            connection.sendPacket(presence);
            presence.setPriority(priority);
            history.setMaxStanzas(0);
            mch = doit.roomConnect(nickInConf, confName, connection);
            PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class));
            connection.addPacketListener(myListener, filter);
           
            // раз в минуту просыпаемся и проверяем, что соединение не разорвано
            while (true)
            {
                mch.addMessageListener(myListener);
                if (mch.isJoined()&& connection.isConnected()) {
                    System.out.println("Смотрю");
                }
                Thread.sleep(6000);
                if(connection.isConnected()==false) break;
            }
        } catch (Exception e) {
            System.out.println("Troubles with connection");
            e.getMessage();
            connection.disconnect();
        }
       }
    }

    /**
     * Обработка входящего сообщения<hr>
     *
     * @param message входящее сообщение
     */
    private void processMessage(Message message) {

        String messageBody = message.getBody();
        String CID = message.getFrom();
        String Nick = doit.SeekForNick(CID);
        // Это идут команды, Начинающиеся с "."
        //Например [01:23:45] <Owner> .голос
        if (messageBody.startsWith(".")) {
            String sentence = messageBody.substring(1);
            if ((sentence.startsWith("голос")) || (sentence.startsWith("Голос"))) 
               toConf(Nick + ": "+"Есть коннект");
        }
        // Тут идут реакции на обращение к боту по имени:
        // Например, [01:23:45] <Owner> Bot: проверка
        if (messageBody.startsWith(nickInConf)) {
            toConf(Nick + " слушаю?");
        }
        //Это идет обработка сообщений на предмет появления нужных слов.
        //Например [01:23:45] <A> проверка необходима
        seekForDate(messageBody, "проверка", "пройдена");
        //Тут идёт обработка роллов
        //Пример - [01:23:45] <Owner> doing roll 1d8
        if (((seekForTrue(messageBody, "ролл")) || (seekForTrue(messageBody, "roll"))
                || (seekForTrue(messageBody, "r")) || (seekForTrue(messageBody, "р"))))        {
            toConf(doit.rollIt(messageBody, Nick));
        }
        if (seekForTrue(messageBody, ")заходит как участник и член")) {
            System.out.println("");
        }
    }

    /**
     * Обработка входящего сообщения<hr>
     *
     * @param message отправленное сообщение
     */
    public void toConf(String message) {
        doit.sendConfMessage(message, mch);
    }

//    private void toConfDelay(String message, int delay) throws InterruptedException {
//        Thread.sleep(delay * 60);
//        toConf(message);
//    }
 /**
     * Проверка наличия команды в сообщении<hr>
     *
     * @param message обрабатываемое сообщение
     * @param command искомая команда
     * @param toWrite текст, который посылает если есть команда в сообщении
     */
    private void seekForDate(String message, String command, String toWrite) {
        //TODO Прикрутить к этому делу HashMap
        StringTokenizer stzr;
        stzr = new StringTokenizer(message, " \t\n\r,:;-!.\"()?");
        while (stzr.hasMoreTokens()) {
            if ((stzr.nextToken().equalsIgnoreCase(command.intern()))) {
                toConf(toWrite);
            }
        }
    }
    /**
     * Проверка наличия фрагмента текста в сообщении<hr>
     *
     * @param message обрабатываемое сообщение
     * @param lookFor искомый фрагмент текста
     * @return true при наличии фрагмента, false в остальных случаях
     */
    private boolean seekForTrue(String message, String lookFor) {
        StringTokenizer stzr;
        stzr = new StringTokenizer(message, " \t\n\r,:;-!.\"()?");
        while (stzr.hasMoreTokens()) {
            if (stzr.nextToken().equalsIgnoreCase(lookFor.intern())) {
                return true;
            }
        }
        return false;
    }
    
}
