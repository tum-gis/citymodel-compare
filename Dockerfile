# Use the official gradle image as a base image
FROM gradle:9.0.0-jdk17 AS build

# Allow access
EXPOSE 7474
EXPOSE 7687

# Set the working directory
WORKDIR /home/gradle/src

# Copy the source code into the container
RUN git clone https://github.com/tum-gis/citymodel-compare

# Set the working directory
WORKDIR /home/gradle/src/citymodel-compare

# Cache Gradle dependencies
RUN gradle dependencies --no-daemon || true

# Build the application
COPY gradlew gradle/ /home/gradle/src/citymodel-compare/
RUN ./gradlew build --no-daemon -x test

# Run the application using Gradle
CMD ["gradle", "run"]
