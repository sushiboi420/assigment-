
package Covid19_2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Receiver2{
    public static void main(String[] args) throws IOException, TimeoutException{
     
      
        ConnectionFactory factoryII = new ConnectionFactory();
                
        Connection connII = factoryII.newConnection();
        
        Channel msgpassingII = connII.createChannel();
        
        msgpassingII.queueDeclare("MessageQueueII",false, false, false, null);
        
        msgpassingII.basicConsume("MessageQueueII", true, (y,message2)->{
            String msgII = new String(message2.getBody());
            System.out.println(msgII);
        }, y ->{});
    }
    
}