# Use the official gradle image as a base image
FROM gradle:jdk17 AS build

# Set the working directory
WORKDIR /home/gradle/src

# Copy the source code into the container
RUN git clone https://github.com/tum-gis/citymodel-compare

# Set the working directory
WORKDIR /home/gradle/src/citymodel-compare

# Run the application using Gradle
CMD ["gradle", "run"]
