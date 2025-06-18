FROM tomcat:10.1.29

WORKDIR /app
COPY build/libs/kyc-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/homefirstops.war

# Copy the tokens directory into a known location
COPY tokens /app/tokens

EXPOSE 8080

RUN mkdir -p /var/www/files/

CMD ["catalina.sh", "run"]