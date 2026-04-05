# Use an official Java 17 JDK image as the build environment
FROM eclipse-temurin:17-jdk AS builder

# Set the working directory
WORKDIR /app

# Copy gradle wrapper and configuration files first to cache dependencies
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./
COPY core/build.gradle core/
COPY desktop/build.gradle desktop/

# Make the wrapper executable
RUN chmod +x ./gradlew

# Copy the rest of the source code and assets
COPY core/ src/core/
COPY desktop/ src/desktop/
COPY assets/ assets/
COPY . .

# Build the project and create the distributable jar
RUN ./gradlew build desktop:dist --no-daemon

# --- Execution Stage ---
# Use a lightweight JRE image for running the app
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the compiled GUI jar and assets from the builder stage
COPY --from=builder /app/desktop/build/libs/*.jar ./citopia.jar
COPY --from=builder /app/assets/ ./assets/

# Command to run the application
# Note: Running a LibGDX visual app in Docker requires X11 socket forwarding from the host
CMD ["java", "-jar", "citopia.jar"]
