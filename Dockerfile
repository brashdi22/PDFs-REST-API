FROM azul/zulu-openjdk-alpine:18
WORKDIR /app
COPY target/*.jar app.jar
COPY src/main/resources/static/stopwords.txt /app/static/stopwords.txt
ENTRYPOINT ["java", "-jar", "app.jar"]