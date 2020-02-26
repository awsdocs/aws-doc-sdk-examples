# Manifest

Component for reading [phar.io](https://phar.io/) manifest information from a [PHP Archive (PHAR)](http://php.net/phar).

[![Build Status](https://travis-ci.org/phar-io/manifest.svg?branch=master)](https://travis-ci.org/phar-io/manifest)
[![Scrutinizer Code Quality](https://scrutinizer-ci.com/g/phar-io/manifest/badges/quality-score.png?b=master)](https://scrutinizer-ci.com/g/phar-io/manifest/?branch=master)
[![SensioLabsInsight](https://insight.sensiolabs.com/projects/d8cc6035-69ad-477d-bd1a-ccc605480fd7/mini.png)](https://insight.sensiolabs.com/projects/d8cc6035-69ad-477d-bd1a-ccc605480fd7)

## Installation

You can add this library as a local, per-project dependency to your project using [Composer](https://getcomposer.org/):

    composer require phar-io/manifest

If you only need this library during development, for instance to run your project's test suite, then you should add it as a development-time dependency:

    composer require --dev phar-io/manifest

## Usage

```php
use PharIo\Manifest\ManifestLoader;
use PharIo\Manifest\ManifestSerializer;

$manifest = ManifestLoader::fromFile('manifest.xml');

var_dump($manifest);

echo (new ManifestSerializer)->serializeToString($manifest);
```
