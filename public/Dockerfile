FROM tomcat:10.1.29

WORKDIR /app
COPY build/libs/public-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/public.war

EXPOSE 8080

RUN mkdir -p /var/www/files/

CMD ["catalina.sh", "run"]