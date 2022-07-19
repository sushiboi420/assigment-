package Covid19_1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receiver1{
    public static void main(String[] args) throws IOException, TimeoutException{
        ConnectionFactory factoryI = new ConnectionFactory();
                
        Connection connI = factoryI.newConnection();
        
        Channel msgpassingI = connI.createChannel();
        
        msgpassingI.queueDeclare("MessageQueueI",false, false, false, null);
        
        msgpassingI.basicConsume("MessageQueueI", true, (x,messageI)->{
            String msgI = new String(messageI.getBody());
            System.out.println(msgI);
        }, x ->{});
    }
}