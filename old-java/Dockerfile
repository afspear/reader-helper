FROM jetty:jre8

RUN rm -rf /var/lib/jetty/webapps/ROOT

COPY target/reader-helper-1.0-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.war

COPY readerhelper-169800-6c9dd1b6c2e5.json /var/lib/jetty/credentials.json

#for running on heroku

CMD java -jar /usr/local/jetty/start.jar jetty.http.port=$PORT

