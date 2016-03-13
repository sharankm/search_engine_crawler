dir /s /B *.java > sources.txt
javac -classpath ./lib/jsoup-1.8.3.jar @sources.txt
java -classpath ./lib/jsoup-1.8.3.jar;./ Crawler seeds.txt 20 20 crawlout