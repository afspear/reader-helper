FROM tomcat:9

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY target/reader-helper-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

#for running on heroku

CMD echo setting port to $PORT && sed -i -e 's/8080/'"$PORT"'/' $CATALINA_HOME/conf/server.xml  && catalina.sh run

