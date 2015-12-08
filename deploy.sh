killall java
nohup java -jar /root/cse391-wiki-1.0.0-SNAPSHOT.jar server /root/config.yaml 2>&1 > /var/log/wiki.log &