<?php

namespace PharIo\Manifest;

use DOMDocument;

class AuthorElementCollectionTest extends \PHPUnit\Framework\TestCase {
    public function testAuthorElementCanBeRetrievedFromCollection() {
        $dom = new DOMDocument();
        $dom->loadXML('<?xml version="1.0" ?><author xmlns="https://phar.io/xml/manifest/1.0" name="Reiner Zufall" email="reiner@zufall.de" />');
        $collection = new AuthorElementCollection($dom->childNodes);

        foreach($collection as $authorElement) {
            $this->assertInstanceOf(AuthorElement::class, $authorElement);
        }
    }

}
