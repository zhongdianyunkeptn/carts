FROM java:openjdk-8-alpine

WORKDIR /usr/src/app
COPY ./target/*.jar ./app.jar
COPY ./MANIFEST /

CMD ["sh", "-c", "cat /MANIFEST && java -Djava.security.egd=file:/dev/urandom -jar ./app.jar --port=8080"]
