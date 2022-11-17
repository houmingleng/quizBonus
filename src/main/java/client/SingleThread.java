package client;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.net.URI;
public class SingleThread implements Runnable {
    private static final Integer HTTP_OK = 200;
    private static final Integer HTTP_CREATED = 201;
    private static final Integer ALLOW_ATTEMPTS_NUM = 5;
    AtomicInteger successCallCount;
    AtomicInteger failCallCount;
    private CopyOnWriteArrayList<URI> recordList;
    CountDownLatch totalLatch;
    private Integer totalCalls;
    public SingleThread (int totalCalls,AtomicInteger successCallCount, AtomicInteger failCallCount,
                         CopyOnWriteArrayList<URI> recordList , CountDownLatch totalLatch){
        this.successCallCount = successCallCount;
        this.failCallCount = failCallCount;
        this.recordList = recordList;
        this.totalLatch = totalLatch;
        this.totalCalls = totalCalls;

    }

    @Override
    public void run() {
        String url = "http://35.90.162.215:8080/quicProject_war/prime";
       // Random random = new Random();

        int curSuccess = 0;
        int curFail = 0;
        int i = 0;
        while (i < totalCalls) {
            Integer val = 0;
            while(val %2 ==0 ){
                val =  ThreadLocalRandom.current().nextInt(1,100000+1);
            }
            url = url+"/"+(int)val;
            int retry = 0;
            while (retry < ALLOW_ATTEMPTS_NUM) {
                try {
                    recordList.add(new URI(url));
                     curSuccess++;
                } catch (Exception e) {
                    retry++;
                    System.out.println(e.getMessage());

                    e.printStackTrace();
                }
            }
            if (retry == ALLOW_ATTEMPTS_NUM) {
                curFail++;
            }
            i++;
        }
        System.out.println("thread #: " + totalLatch.getCount());
        totalLatch.countDown();
        successCallCount.getAndAdd(curSuccess);
        failCallCount.getAndAdd(curFail);


    }



    }

