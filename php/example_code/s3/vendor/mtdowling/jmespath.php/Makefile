all: clean coverage

test:
	vendor/bin/phpunit

coverage:
	vendor/bin/phpunit --coverage-html=artifacts/coverage

view-coverage:
	open artifacts/coverage/index.html

clean:
	rm -rf artifacts/*
	rm -rf compiled/*

perf:
	php bin/perf.php

.PHONY: test coverage perf
