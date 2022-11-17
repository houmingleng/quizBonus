package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.time.Duration;

public class Client {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static CopyOnWriteArrayList<URI> recordList = new CopyOnWriteArrayList<>();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .executor(executorService)
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();



    public static void main(String[] args) throws Exception {

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        CountDownLatch totalLatch = new CountDownLatch(200000);
        for(int i = 0; i < 200; i++) {

            SingleThread t = new SingleThread(100000/200 , success, failure,recordList
                    ,totalLatch);
            Thread thread = new Thread(t);
            thread.start();
        }

AtomicInteger isprime = new AtomicInteger(0);
    List<CompletableFuture<String>> result = recordList.stream()
            .map(url -> httpClient.sendAsync(
                            HttpRequest.newBuilder(url)
                                    .GET()
                                    .setHeader("User-Agent", "Java 11 HttpClient Bot")
                                    .build(),
                            HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.body()))
            .collect(Collectors.toList());

        for(CompletableFuture<String> future :result) {

        if(future.get().equals("Is prime")){
            isprime.getAndAdd(1);
        }
      }
        System.out.println(isprime.get()*1.0 / 100000);
    }
}

