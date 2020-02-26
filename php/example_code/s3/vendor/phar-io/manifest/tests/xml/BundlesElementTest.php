<?php

namespace PharIo\Manifest;

use DOMDocument;

class BundlesElementTest extends \PHPUnit\Framework\TestCase {
    /**
     * @var DOMDocument
     */
    private $dom;

    /**
     * @var BundlesElement
     */
    private $bundles;

    protected function setUp() {
        $this->dom = new DOMDocument();
        $this->dom->loadXML('<?xml version="1.0" ?><bundles xmlns="https://phar.io/xml/manifest/1.0" />');
        $this->bundles = new BundlesElement($this->dom->documentElement);
    }

    public function testThrowsExceptionWhenGetComponentElementsIsCalledButNodesAreMissing() {
        $this->expectException(ManifestElementException::class);
        $this->bundles->getComponentElements();
    }

    public function testGetComponentElementsReturnsComponentElementCollection() {
        $this->addComponent();
        $this->assertInstanceOf(
            ComponentElementCollection::class, $this->bundles->getComponentElements()
        );
    }

    private function addComponent() {
        $this->dom->documentElement->appendChild(
            $this->dom->createElementNS('https://phar.io/xml/manifest/1.0', 'component')
        );
    }
}
