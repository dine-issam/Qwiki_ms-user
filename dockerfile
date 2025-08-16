# Use OpenJDK 23 as the base image
FROM openjdk:23

# Copy the JAR file from the target directory and rename it to ms-patient.jar
COPY target/ms-user*.jar /ms-user.jar

# Run the JAR file with Java when the container starts
CMD ["java", "-jar", "/ms-user.jar"]
