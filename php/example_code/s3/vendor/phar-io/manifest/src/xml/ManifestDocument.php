<?php
/*
 * This file is part of PharIo\Manifest.
 *
 * (c) Arne Blankerts <arne@blankerts.de>, Sebastian Heuer <sebastian@phpeople.de>, Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace PharIo\Manifest;

use DOMDocument;
use DOMElement;

class ManifestDocument {
    const XMLNS = 'https://phar.io/xml/manifest/1.0';

    /**
     * @var DOMDocument
     */
    private $dom;

    /**
     * ManifestDocument constructor.
     *
     * @param DOMDocument $dom
     */
    private function __construct(DOMDocument $dom) {
        $this->ensureCorrectDocumentType($dom);

        $this->dom = $dom;
    }

    public static function fromFile($filename) {
        if (!file_exists($filename)) {
            throw new ManifestDocumentException(
                sprintf('File "%s" not found', $filename)
            );
        }

        return self::fromString(
            file_get_contents($filename)
        );
    }

    public static function fromString($xmlString) {
        $prev = libxml_use_internal_errors(true);
        libxml_clear_errors();

        $dom = new DOMDocument();
        $dom->loadXML($xmlString);

        $errors = libxml_get_errors();
        libxml_use_internal_errors($prev);

        if (count($errors) !== 0) {
            throw new ManifestDocumentLoadingException($errors);
        }

        return new self($dom);
    }

    public function getContainsElement() {
        return new ContainsElement(
            $this->fetchElementByName('contains')
        );
    }

    public function getCopyrightElement() {
        return new CopyrightElement(
            $this->fetchElementByName('copyright')
        );
    }

    public function getRequiresElement() {
        return new RequiresElement(
            $this->fetchElementByName('requires')
        );
    }

    public function hasBundlesElement() {
        return $this->dom->getElementsByTagNameNS(self::XMLNS, 'bundles')->length === 1;
    }

    public function getBundlesElement() {
        return new BundlesElement(
            $this->fetchElementByName('bundles')
        );
    }

    private function ensureCorrectDocumentType(DOMDocument $dom) {
        $root = $dom->documentElement;

        if ($root->localName !== 'phar' || $root->namespaceURI !== self::XMLNS) {
            throw new ManifestDocumentException('Not a phar.io manifest document');
        }
    }

    /**
     * @param $elementName
     *
     * @return DOMElement
     *
     * @throws ManifestDocumentException
     */
    private function fetchElementByName($elementName) {
        $element = $this->dom->getElementsByTagNameNS(self::XMLNS, $elementName)->item(0);

        if (!$element instanceof DOMElement) {
            throw new ManifestDocumentException(
                sprintf('Element %s missing', $elementName)
            );
        }

        return $element;
    }
}
