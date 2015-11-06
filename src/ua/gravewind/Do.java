/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.gravewind;


import java.util.Random;
import java.util.StringTokenizer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class Do {
     /**
     * Обработка входящего сообщения<hr>
     *
     * @param message отправленное сообщение
     * @param cht конференция
     */
    protected void sendConfMessage(String message, MultiUserChat cht) {
        if (!message.equals("")) {
            try {
                cht.sendMessage(message);
            } catch (XMPPException e) {
                System.out.println("No message delivered!");//e.getMessage());
            }
        }
    }
        /**
     * Отправка личного сообщения пользователю<hr>
     *
     * @param to JID пользователя, которому надо отправить сообщение
     * @param message сообщение
     */
    private void sendPrivateMessage(String to, String message, XMPPConnection connection) {
        if (!message.equals("")) {
            ChatManager chatmanager = connection.getChatManager();
            Chat newChat = chatmanager.createChat(to, null);
            try {
                newChat.sendMessage(message);
            } catch (XMPPException e) {
                e.getMessage();
            }
        }
    }

    protected String SeekForNick(String income){
        char[] ch = income.toCharArray();
        int i = 0;
        do  i++;
        while (ch[i] != '/');
        return income.substring(++i);
    }
    /**
     * Подключение к конференции
     * 
     * @param RoomName название конференции
     * @param BotName ник бота в конференции
     * @param connection ...
     **/
    protected MultiUserChat roomConnect(String BotName, String RoomName, Connection connection) {
        MultiUserChat conf_ = new MultiUserChat(connection, RoomName);
        try {
            //Здесь бот подключается в конференцию
            conf_.join(BotName);
            //А здесь можно инициалиризовать действия, выполняющиеся единократно после подключения
            conf_.sendMessage("Рад видеть вас. Снова.");
        } catch (XMPPException ex) {
            System.out.println(ex.getMessage());
        }
        return conf_;
    }
  /**
   * Обработка ролла. Максимальное количество кубиков - 1000
   * 
   * @param message обрабатываемое сообщение
   * @param nick ник отправителя
   * @return сообщение для отправки в конференцию
   **/
  protected String rollIt(String message, String nick)
             {
                StringTokenizer str=new StringTokenizer(message);
                Random rand=new Random();
                String iterlist=" dкдk";
                while(str.hasMoreTokens()){
                    String a=str.nextToken(iterlist);
                    if (checkString(a)){
                        if(Integer.parseInt(a)<1000){
                            if (str.hasMoreTokens()){
                                String b= str.nextToken(iterlist);
                                if (checkString(b)&&b!=null){
                                    String toSend= "/me говорит, что "+nick+" бросил кубики на ";
                                    for(int i=0;i<Integer.parseInt(a);i++)
                                        {
                                            toSend=toSend+ "|"+String.valueOf(rand.nextInt(Integer.parseInt(b))+1);
                                        }
                                    toSend=toSend+"|.";
                                    return toSend;
                                }
                            }
                        }
                        return "СЛИШКОМ много дайсов";
                    }
                }
                return "Что-то не так.";
            }
  
  public boolean checkString(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}