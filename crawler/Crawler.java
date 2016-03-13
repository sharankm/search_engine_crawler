import java.util.List;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java Crawler <seed-filename> <num-pages> <hops-away> <output-dir-name>");
            System.exit(0);
        }

        String seedFileName = args[0];
        AtomicInteger pageCounter = new AtomicInteger(Integer.parseInt(args[1]));
        int numHops = Integer.parseInt(args[2]);
        String directoryName = args[3];

        File dirFile = null;
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
        ConcurrentMap<String, String> urlMap = new ConcurrentHashMap<>();
        try {
            dirFile = new File(directoryName);
            if (!dirFile.mkdir()) {
                System.out.print("Could not create Directory: " + directoryName);
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.print("Could not create Directory: " + directoryName);
        }

        SeedFileReader seedFileReader = new SeedFileReader();
        List<String> seedList = seedFileReader.getSeedList(seedFileName);
        System.out.println("Crawling in Progress...");
        for (String seed : seedList) {
            queue.add(seed);
            SeedManager seedManager = new SeedManager(dirFile, queue, urlMap);
            seedManager.setHopAndCount(numHops, pageCounter);
            seedManager.start();
        }
    }
}