FROM jetty:jre8

RUN rm -rf /var/lib/jetty/webapps/ROOT

COPY target/reader-helper-1.0-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.war

COPY ReaderHelper-53a7a7a3d474.json /var/lib/jetty/ReaderHelper-53a7a7a3d474.json

#for running on heroku

CMD java -jar /usr/local/jetty/start.jar jetty.http.port=$PORT

