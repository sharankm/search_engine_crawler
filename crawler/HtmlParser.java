
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HtmlParser extends Thread {
    private volatile String url;
    private File dirFile;
    private ConcurrentLinkedQueue<String> queue;
    private ConcurrentMap<String, String> urlMap;
    private AtomicInteger hopCounter;
    private AtomicInteger pageCounter;

    public HtmlParser(File dirFile, ConcurrentLinkedQueue<String> queue, ConcurrentMap<String, String> urlMap) {
        this.dirFile = dirFile;
        this.queue = queue;
        this.urlMap = urlMap;
    }

    public void crawl() {
        try {
            url = queue.remove();
            pageCounter.decrementAndGet();
            hopCounter.decrementAndGet();
            if (new RobotReader().canRead(url)) {
                Connection connection = Jsoup.connect(url);
                connection.timeout(5000);
                connection.followRedirects(true);
                connection.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");
                Document doc = connection.get();
                String htmlContent = doc.html();

                if (htmlContent != null) {
                    Elements links = doc.select("a[href^=http:]");
                    for (Element link : links) {
                        if (!urlMap.containsKey(link.attr("abs:href"))) {
                            queue.add(link.attr("abs:href"));
                        }
                    }
                    File oFile = new File("linkFileMap.txt");
                    if (!oFile.exists()) {
                        oFile.createNewFile();
                    }
                    File newFile = File.createTempFile("edu", ".html", dirFile);
                    BufferedWriter out = new BufferedWriter(new FileWriter(newFile));
                    out.write(htmlContent);
                    out.close();
                    if (oFile.canWrite()) {
                        BufferedWriter oWriter = new BufferedWriter(new FileWriter("linkFileMap.txt", true));
                        oWriter.write(url + "|" + newFile.getName() + "\n");
                        oWriter.close();
                        urlMap.put(url, url);
                    }
                }
            }
        } catch (MalformedURLException ex) {
            //         System.out.println("The seed URL: " + url + " is malformed");
        } catch (IOException ex) {
            //         System.out.println("Error while writing into file from URL: " + url + ex);
        }
    }

    @Override
    public void run() {
        if ((hopCounter.get() > 0) && (queue.size() > 0)) {
            crawl();
        }
    }

    public void setHopAndCount(AtomicInteger hopCounter, AtomicInteger pageCounter) {
        this.hopCounter = hopCounter;
        this.pageCounter = pageCounter;
    }
}