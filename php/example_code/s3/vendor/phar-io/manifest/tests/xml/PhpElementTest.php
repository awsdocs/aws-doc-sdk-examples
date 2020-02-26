<?php

namespace PharIo\Manifest;

use DOMDocument;

class PhpElementTest extends \PHPUnit\Framework\TestCase {
    /**
     * @var DOMDocument
     */
    private $dom;

    /**
     * @var PhpElement
     */
    private $php;

    protected function setUp() {
        $this->dom = new DOMDocument();
        $this->dom->loadXML('<?xml version="1.0" ?><php xmlns="https://phar.io/xml/manifest/1.0" version="^5.6 || ^7.0" />');
        $this->php = new PhpElement($this->dom->documentElement);
    }

    public function testVersionConstraintCanBeRetrieved() {
        $this->assertEquals('^5.6 || ^7.0', $this->php->getVersion());
    }

    public function testHasExtElementsReturnsFalseWhenNoExtensionsAreRequired() {
        $this->assertFalse($this->php->hasExtElements());
    }

    public function testHasExtElementsReturnsTrueWhenExtensionsAreRequired() {
        $this->addExtElement();
        $this->assertTrue($this->php->hasExtElements());
    }

    public function testGetExtElementsReturnsExtElementCollection() {
        $this->addExtElement();
        $this->assertInstanceOf(ExtElementCollection::class, $this->php->getExtElements());
    }

    private function addExtElement() {
        $this->dom->documentElement->appendChild(
            $this->dom->createElementNS('https://phar.io/xml/manifest/1.0', 'ext')
        );
    }

}
