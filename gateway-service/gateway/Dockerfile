FROM openjdk:21-jdk
# Set the working directory
WORKDIR /opt/app
#ENV PORT 8080
# Expose the port on which the application will run
EXPOSE 8080
# Copy the application JAR file to the container
COPY target/*.jar app.jar
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
