<?php

namespace PharIo\Manifest;

use DOMDocument;

class RequiresElementTest extends \PHPUnit\Framework\TestCase {
    /**
     * @var DOMDocument
     */
    private $dom;

    /**
     * @var RequiresElement
     */
    private $requires;

    protected function setUp() {
        $this->dom = new DOMDocument();
        $this->dom->loadXML('<?xml version="1.0" ?><requires xmlns="https://phar.io/xml/manifest/1.0" />');
        $this->requires = new RequiresElement($this->dom->documentElement);
    }

    public function testThrowsExceptionWhenGetPhpElementIsCalledButElementIsMissing() {
        $this->expectException(ManifestElementException::class);
        $this->requires->getPHPElement();
    }

    public function testHasExtElementsReturnsTrueWhenExtensionsAreRequired() {
        $this->dom->documentElement->appendChild(
            $this->dom->createElementNS('https://phar.io/xml/manifest/1.0', 'php')
        );

        $this->assertInstanceOf(PhpElement::class, $this->requires->getPHPElement());
    }

}
