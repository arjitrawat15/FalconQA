# FalconQA Docker Image
FROM maven:3.8-openjdk-11-slim

# Install Chrome
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Download dependencies
RUN mvn dependency:resolve

# Set environment variables
ENV MAVEN_OPTS="-Xmx2048m -Xms512m"
ENV HEADLESS=true

# Create output directories
RUN mkdir -p test-output/reports test-output/screenshots test-output/logs

# Entry point
ENTRYPOINT ["mvn", "clean", "test"]

# Default command - can be overridden
CMD ["-Dbrowser=chrome", "-Dheadless=true"]
