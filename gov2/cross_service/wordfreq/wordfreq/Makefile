.PHONY: clean download all 

download:
	go mod download

clean:
	rm -f uploader worker
uploader: download
	go build -o uploader ./cmd/uploader/
worker: download 
	go build -o worker ./cmd/worker/

all: worker uploader

container:
	docker build . 
