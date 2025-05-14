# Use Gradle base image with JDK 21
FROM gradle:8.5.0-jdk21

# Install system dependencies
RUN apt-get update -qq && \
    DEBIAN_FRONTEND=noninteractive apt-get install -qq -y \
        python3 \
        python3-pip \
        git \
        curl \
        vim \
        less \
        ca-certificates && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Install Python dependencies
RUN pip3 install --quiet --no-cache-dir boto3

# Set working directory
WORKDIR /app

# Copy only the Python script (it clones code later)
COPY run_tests.py .

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /app 
USER automation:automation

# Default entrypoint (optional: run script automatically)
CMD ["python3", "run_tests.py"]
