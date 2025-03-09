# Use official OpenJDK 17 base image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file into the container
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app will run on
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
