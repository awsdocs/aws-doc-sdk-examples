.PHONY: install-phive
install-phive:
	mkdir tools; \
	wget -O tools/phive.phar https://phar.io/releases/phive.phar; \
	wget -O tools/phive.phar.asc https://phar.io/releases/phive.phar.asc; \
	gpg --keyserver pool.sks-keyservers.net --recv-keys 0x9D8A98B29B2D5D79; \
	gpg --verify tools/phive.phar.asc tools/phive.phar; \
	chmod +x tools/phive.phar

.PHONY: setup
setup: install-phive
	docker run -it --rm -v${PWD}:/opt/project -w /opt/project phpdoc/phar-ga:latest php tools/phive.phar install --force-accept-unsigned

.PHONY: phpcs
phpcs:
	docker run -it --rm -v${PWD}:/opt/project -w /opt/project phpdoc/phpcs-ga:v1.0.0 -s

.PHONY: phpcbf
phpcbf:
	docker run -it --rm -v${PWD}:/opt/project -w /opt/project phpdoc/phpcs-ga:v1.0.0 phpcbf

.PHONY: phpstan
phpstan:
	docker run -it --rm -v${PWD}:/opt/project -w /opt/project phpdoc/phpstan-ga:latest analyse src --no-progress --level max --configuration phpstan.neon

.PHONY: psaml
psalm:
	docker run -it --rm -v${PWD}:/opt/project -w /opt/project mickaelandrieu/psalm-ga

.PHONY: test
test:
	docker run -it --rm -v${PWD}:/opt/project -w /opt/project php:7.4-pcov tools/phpunit

.PHONY: pre-commit-test
pre-commit-test: test phpcs phpstan psalm

