FROM eclipse-temurin:21-jre-alpine
COPY expense/tracker-rest/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
