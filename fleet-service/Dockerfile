FROM eclipse-temurin:21-jre-alpine
RUN apk update && apk upgrade --no-cache
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]