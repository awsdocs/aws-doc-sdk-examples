# Use the official Alpine Linux image as the base image
FROM alpine:latest

# Set the working directory to /app
WORKDIR /app

# Copy the "hello.sh" script into the container
COPY hello.sh .

# Make the "hello.sh" script executable
RUN chmod +x hello.sh

# Define the command to run the "hello.sh" script
CMD ["./hello.sh"]