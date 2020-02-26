<?php

namespace PharIo\Manifest;

class LicenseElementTest extends \PHPUnit\Framework\TestCase {
    /**
     * @var LicenseElement
     */
    private $license;

    protected function setUp() {
        $dom = new \DOMDocument();
        $dom->loadXML('<?xml version="1.0" ?><license xmlns="https://phar.io/xml/manifest/1.0" type="BSD-3" url="https://some.tld/LICENSE" />');
        $this->license = new LicenseElement($dom->documentElement);
    }

    public function testTypeCanBeRetrieved() {
        $this->assertEquals('BSD-3', $this->license->getType());
    }

    public function testUrlCanBeRetrieved() {
        $this->assertEquals('https://some.tld/LICENSE', $this->license->getUrl());
    }

}
