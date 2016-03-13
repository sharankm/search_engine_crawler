import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotReader {

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> map
            = new ConcurrentHashMap<>();
    private void parseRobotsFileString(String site, String robotsFile) {
        int currentIndex = 0;
        Matcher userAgentLine = Pattern.compile("(?i)User-Agent:\\s*(.*)")
                .matcher(robotsFile);
        Matcher disallowLine = Pattern.compile("(?i)Disallow:\\s*(.*)")
                .matcher(robotsFile);
        Matcher blankLine = Pattern.compile("\n\\s*\n").matcher(robotsFile);


        ConcurrentHashMap<String, Boolean> m = map.get(site);
        if (m == null) {
            m = new ConcurrentHashMap<>();
            map.put(site, m);
        }
        while (userAgentLine.find()) {
            if (userAgentLine.group(1).indexOf('*') != -1) {
                currentIndex = userAgentLine.end();
                blankLine.region(currentIndex, robotsFile.length());
                int blankLineIndex = robotsFile.length();
                if (blankLine.find())
                    blankLineIndex = blankLine.start();
                disallowLine.region(currentIndex, blankLineIndex);
                while (disallowLine.find()) {
                    String disallowed = disallowLine.group(1).trim();
                    if (disallowed.length() > 0) {
                        if (disallowed.endsWith("/"))
                            disallowed = disallowed.substring(0,
                                    disallowed.lastIndexOf('/'));
                    }
                    m.put(disallowed, true);
                }
            }
        }
    }

    public boolean canRead(String url) {
        try {
            URL u = url(url);
            String site = u.getHost();
            if (! map.containsKey(site)) {
                String robotText = readRobotsFile(url("http://" + site + "/robots.txt"));
                if (robotText != null) {
                    parseRobotsFileString(site, robotText);
                }
            }

            ConcurrentHashMap<String, Boolean> m = map.get(site);
            if (m == null) {
                return true;
            } else {
                String path = u.getPath();
                if (path != null && path.length() > 0 && path.endsWith("/"))
                    path = path.substring(0, path.lastIndexOf('/'));
                return ! m.containsKey(path);
            }

        } catch (MalformedURLException e) {
 //           System.out.println("RobotReader: " + e.toString());
        } catch (Exception e) {
 //           System.out.println("RobotReader: " + e.toString());
        }
        return true;
    }

    private URL url(String url) throws MalformedURLException {
        return new URL(url);
    }

    public String readRobotsFile(URL urlObj) throws IOException {
        StringBuffer page = new StringBuffer();
        BufferedReader x = new BufferedReader(new InputStreamReader(urlObj
                .openConnection().getInputStream()));
        String line = "";
        while ((line=x.readLine())!=null) {
            page.append(line + "\n");
        }
        x.close();
        return page.toString();
    }
}
