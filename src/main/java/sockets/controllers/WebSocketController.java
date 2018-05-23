package sockets.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate template;
    private List liveDataTest = new ArrayList();

    @Autowired
    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/responseData")
    public JSONObject onReceivedMessage(String message) {
        System.out.println(message);
        this.template.convertAndSend("/data", makeRandomData(220));
        System.out.println(this.template.getUserDestinationPrefix());

        this.template.convertAndSend("/data", makeRandomData(2000));
        System.out.println("Received Msg in OnReceivedMessage");
        Object obj = new Object();
        int timesOfLoop = 0;
        try {
            synchronized (obj) {
                while (timesOfLoop < 5) {//Or any Loops
                    this.template.convertAndSend("/data", makeRandomData(22));
                    obj.wait(1000);//Sample obj.wait(1000); 1 second sleep
                    timesOfLoop++;
                    System.out.println("Created an Object");
                }
            }
        } catch (InterruptedException ex) {
        }
        System.out.println("poop");
        return makeRandomData(4000);
    }

    private void onDataAction() {
        this.template.convertAndSend("/liveData", "Livedataaaaaaa");

    }


    private JSONObject makeRandomData(int i) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("data1", ThreadLocalRandom.current().nextInt(0, 9 + 1));
            jsonObject.put("data2", ThreadLocalRandom.current().nextInt(0, 9 + 1));
            jsonObject.put("data3", ThreadLocalRandom.current().nextInt(0, 9 + 1));
            jsonObject.put("data4", ThreadLocalRandom.current().nextInt(0, 9 + 1));
            jsonObject.put("data5", ThreadLocalRandom.current().nextInt(0, 9 + 1));
            jsonObject.put("timestamp", System.currentTimeMillis());

            liveDataTest.add(jsonObject);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    @SubscribeMapping("/data")
    public List initDataConnection() {
        makeRandomData(1);
        System.out.println("Subscription on data");
        return liveDataTest;
    }
}
