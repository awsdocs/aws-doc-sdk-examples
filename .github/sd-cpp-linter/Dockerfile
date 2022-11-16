FROM ubuntu:20.04

COPY requirements.txt /requirements.txt

RUN apt update && \
    DEBIAN_FRONTEND=noninteractive \
    apt-get install -y --no-install-recommends\
    clang-tidy-12 \
    python3 python3-pip && \
    pip3 install --upgrade pip && \
    pip3 install -r requirements.txt

COPY sd_review.py /sd_review.py

ENTRYPOINT ["/sd_review.py"]
