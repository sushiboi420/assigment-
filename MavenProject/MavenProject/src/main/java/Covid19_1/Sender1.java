package Covid19_1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Sender1 {
        public static void main(String[] args) throws IOException, TimeoutException, InterruptedException{
         LinkedBlockingQueue<HttpResponse<String>> queueI = new LinkedBlockingQueue<>();
         ScheduledExecutorService x = Executors.newScheduledThreadPool(2);
         x.scheduleAtFixedRate(new getData(queueI), 0, 5, TimeUnit.SECONDS); //retrieve data from API message and put in queue
         x.scheduleAtFixedRate(new countryData(queueI), 0, 1, TimeUnit.SECONDS); //retreve data from queue and put in messaging facility 
     
           
    }   
}
class countryData implements Runnable{
LinkedBlockingQueue<HttpResponse<String>> queueI;
HttpResponse<String> response;

public countryData(LinkedBlockingQueue queueI){
this.queueI = queueI;
}
//connectionfactory
private void connectfac() throws IOException, TimeoutException, InterruptedException{
    String messageI = "Country cases retrieved: "+response.body();
        ConnectionFactory factry = new ConnectionFactory();
        try(Connection connI = factry.newConnection()){
            Channel msgpass = connI.createChannel();
            msgpass.queueDeclare("MessageQueueI", false, false, false, null);
            msgpass.basicPublish("", "MessageQueueI", false, null, messageI.getBytes());
            System.out.println("Message sent out.");
                      
}
}

 @Override
 //retrieve the data from queue
public void run() {
try {
response = queueI.take();
} catch (InterruptedException ex) {
Logger.getLogger(countryData.class.getName()).log(Level.SEVERE, null, ex);
}
//run connectfac if queue is empty
if(queueI.isEmpty()){
    try {
        this.connectfac();
    } catch (Exception ex) {}
}
}
}

//random selection of country
class getData implements Runnable{

LinkedBlockingQueue<HttpResponse<String>> queueI;
HttpResponse<String> response;
String countryI [] = {"Malaysia", "Japan", "Singapore","Indonesia","Cambodia"};
Random ra = new Random();
public getData(LinkedBlockingQueue queueI){
this.queueI = queueI;
}
@Override
//API
public void run(){

    int ctry = ra.nextInt(countryI.length);
   HttpRequest request = HttpRequest.newBuilder()
.uri(URI.create("https://covid-19-tracking.p.rapidapi.com/v1/"+countryI[ctry]))
.header("x-rapidapi-key", "0450ed1a94mshb8eeb3e92327bd7p11703ajsn86bf7bbfd5dd")
.header("x-rapidapi-host", "covid-19-tracking.p.rapidapi.com")
.method("GET", HttpRequest.BodyPublishers.noBody())
.build();

try {
response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
} catch (IOException | InterruptedException ex) {
Logger.getLogger(getData.class.getName()).log(Level.SEVERE, null, ex);
}

//put covid data into queue 
try{
queueI.put(response);
}catch(Exception e){};

}
}

