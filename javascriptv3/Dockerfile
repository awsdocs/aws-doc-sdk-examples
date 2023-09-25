# syntax=docker/dockerfile:1
FROM node:18

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /javascriptv3
WORKDIR /javascriptv3

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /javascriptv3/
USER automation:automation

# Perform build steps
RUN npm install
RUN export TEST_SCOPE=integration npm test

# Set default command
CMD ["npm", "test"]