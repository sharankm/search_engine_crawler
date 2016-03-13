import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SeedManager extends Thread {
    private File dirFile;
    private ConcurrentLinkedQueue<String> queue;
    private ConcurrentMap<String, String> urlMap;
    private AtomicInteger hopCounter;
    private AtomicInteger pageCounter;

    public SeedManager(File dirFile, ConcurrentLinkedQueue<String> queue, ConcurrentMap<String, String> urlMap) {
        this.dirFile = dirFile;
        this.queue = queue;
        this.urlMap = urlMap;
    }

    public void crawlSeed() {
        while (pageCounter.get() > 0 && hopCounter.get() > 0) {
            HtmlParser htmlParser = new HtmlParser(dirFile, queue, urlMap);
            htmlParser.setHopAndCount(hopCounter, pageCounter);
            htmlParser.start();
        }
    }

    @Override
    public void run() {
        crawlSeed();
    }

    public void setHopAndCount(int numHops, AtomicInteger pageCounter) {
        this.pageCounter = pageCounter;
        hopCounter = new AtomicInteger(numHops);
    }
}