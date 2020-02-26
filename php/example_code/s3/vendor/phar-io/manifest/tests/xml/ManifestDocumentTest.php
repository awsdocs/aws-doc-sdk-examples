<?php

namespace PharIo\Manifest;

class ManifestDocumentTest extends \PHPUnit\Framework\TestCase {
    public function testThrowsExceptionWhenFileDoesNotExist() {
        $this->expectException(ManifestDocumentException::class);
        ManifestDocument::fromFile('/does/not/exist');
    }

    public function testCanBeCreatedFromFile() {
        $this->assertInstanceOf(
            ManifestDocument::class,
            ManifestDocument::fromFile(__DIR__ . '/../_fixture/phpunit-5.6.5.xml')
        );
    }

    public function testCaneBeConstructedFromString() {
        $content = file_get_contents(__DIR__ . '/../_fixture/phpunit-5.6.5.xml');
        $this->assertInstanceOf(
            ManifestDocument::class,
            ManifestDocument::fromString($content)
        );
    }

    public function testThrowsExceptionOnInvalidXML() {
        $this->expectException(ManifestDocumentLoadingException::class);
        ManifestDocument::fromString('<?xml version="1.0" ?><root>');
    }

    public function testLoadingDocumentWithWrongRootNameThrowsException() {
        $this->expectException(ManifestDocumentException::class);
        ManifestDocument::fromString('<?xml version="1.0" ?><root />');
    }

    public function testLoadingDocumentWithWrongNamespaceThrowsException() {
        $this->expectException(ManifestDocumentException::class);
        ManifestDocument::fromString('<?xml version="1.0" ?><phar xmlns="foo:bar" />');
    }

    public function testContainsElementCanBeRetrieved() {
        $this->assertInstanceOf(
            ContainsElement::class,
            $this->loadFixture()->getContainsElement()
        );
    }

    public function testRequiresElementCanBeRetrieved() {
        $this->assertInstanceOf(
            RequiresElement::class,
            $this->loadFixture()->getRequiresElement()
        );
    }

    public function testCopyrightElementCanBeRetrieved() {
        $this->assertInstanceOf(
            CopyrightElement::class,
            $this->loadFixture()->getCopyrightElement()
        );
    }

    public function testBundlesElementCanBeRetrieved() {
        $this->assertInstanceOf(
            BundlesElement::class,
            $this->loadFixture()->getBundlesElement()
        );
    }

    public function testThrowsExceptionWhenContainsIsMissing() {
        $this->expectException(ManifestDocumentException::class);
        $this->loadEmptyFixture()->getContainsElement();
    }

    public function testThrowsExceptionWhenCopyirhgtIsMissing() {
        $this->expectException(ManifestDocumentException::class);
        $this->loadEmptyFixture()->getCopyrightElement();
    }

    public function testThrowsExceptionWhenRequiresIsMissing() {
        $this->expectException(ManifestDocumentException::class);
        $this->loadEmptyFixture()->getRequiresElement();
    }

    public function testThrowsExceptionWhenBundlesIsMissing() {
        $this->expectException(ManifestDocumentException::class);
        $this->loadEmptyFixture()->getBundlesElement();
    }

    public function testHasBundlesReturnsTrueWhenBundlesNodeIsPresent() {
        $this->assertTrue(
            $this->loadFixture()->hasBundlesElement()
        );
    }

    public function testHasBundlesReturnsFalseWhenBundlesNoNodeIsPresent() {
        $this->assertFalse(
            $this->loadEmptyFixture()->hasBundlesElement()
        );
    }

    private function loadFixture() {
        return ManifestDocument::fromFile(__DIR__ . '/../_fixture/phpunit-5.6.5.xml');
    }

    private function loadEmptyFixture() {
        return ManifestDocument::fromString(
            '<?xml version="1.0" ?><phar xmlns="https://phar.io/xml/manifest/1.0" />'
        );
    }
}
