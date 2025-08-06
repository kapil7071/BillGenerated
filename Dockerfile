FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/BillGenerated-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-Xmx1024m", "-jar", "app.jar"]
