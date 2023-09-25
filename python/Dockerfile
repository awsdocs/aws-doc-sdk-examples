# syntax=docker/dockerfile:1
FROM python:3

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /python
WORKDIR /python

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /python
USER automation:automation

# Perform build steps
RUN python -m pip install -r /python/test_tools/requirements.txt
RUN curl https://certs.secureserver.net/repository/sf-class2-root.crt --output sf-class2-root.crt

CMD ["python", "-m", "python.test_tools.run_all_tests", "--integ"]
