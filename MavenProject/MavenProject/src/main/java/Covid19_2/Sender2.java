package Covid19_2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender2{
    public static void main(String [] args) throws IOException, TimeoutException{
    
    ExecutorService data_process = Executors.newCachedThreadPool();
    LinkedBlockingQueue<HttpResponse<String>> QueueII = new LinkedBlockingQueue();
    data_a ctry = new data_a();
    Future<data_a> st  = data_process.submit(new getData(ctry));
    
    for(int i=0;i<10;i++){
        data_process.submit(new datac(QueueII));
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sender2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   data_process.shutdown();
    
    if(st.isDone()){
        String messageII = "Country cases obtained by the receiver.";
        ConnectionFactory factoryII = new ConnectionFactory();
        try(Connection connII = factoryII.newConnection()){
            Channel msgpassingII = connII.createChannel();
            msgpassingII.queueDeclare("MessageQueueII", false, false, false, null);
            msgpassingII.basicPublish("", "MessageQueueII", false, null, messageII.getBytes()); 
            System.out.println("Country cases sent to receiver.");
                 
        }
    }
    
    }
}


class data_a{
    boolean st = false;    
}

class datac implements Runnable{
    LinkedBlockingQueue<HttpResponse<String>> queueII;
    HttpResponse<String> responseII;
    String countryII [] = {"Malaysia", "Japan","Singapore","Indonesia","Cambodia"};
   Random ra = new Random();
    
   

   
    public datac(LinkedBlockingQueue queueII){
        this.queueII= queueII;
    }
     
    private void Covid_api() throws IOException, InterruptedException{
        
        int ctry = ra.nextInt(countryII.length);
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://covid-19-tracking.p.rapidapi.com/v1/"+countryII[ctry]))
        .header("x-rapidapi-key", "0450ed1a94mshb8eeb3e92327bd7p11703ajsn86bf7bbfd5dd")
        .header("x-rapidapi-host", "covid-19-tracking.p.rapidapi.com")
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
        responseII = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        
    queueII.put(responseII);
    
   }
    
    @Override
    public void run(){
        
        try {
            this.Covid_api();
        } catch (IOException ex) {
            Logger.getLogger(datac.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(datac.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        try {
            queueII.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(datac.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(queueII.isEmpty()){
           
        System.out.println("Available cases: \n" + responseII.body());       
        }
        
    }
}
class getData implements Callable<data_a> {

    data_a ctry;

    public getData(data_a c){
        this.ctry = c;
    }

    @Override
    public data_a call() throws Exception {
        System.out.println("Cases are being retrieved...");
        System.out.println("");
        Thread.sleep(1200);
        ctry.st = true;
        System.out.println("Cases received!");
        Thread.sleep(1200);
        return ctry;
    }
}
    