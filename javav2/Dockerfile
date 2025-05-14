# Use Python with Java and Git preinstalled
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Silence all apt-get output and install Python, pip, git, etc.
RUN apt-get update -qq && \
    DEBIAN_FRONTEND=noninteractive apt-get install -qq -y \
    python3 python3-pip git curl vim less ca-certificates \
    > /dev/null 2>&1 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Install boto3 quietly using pip
RUN pip3 install --quiet --no-cache-dir boto3 > /dev/null 2>&1

# Set workdir
WORKDIR /app

# Copy Python script into container
COPY run_tests.py .

# Run Python script on container start
CMD ["python3", "run_tests.py"]
