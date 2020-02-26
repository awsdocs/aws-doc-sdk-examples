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

use PHPUnit\Framework\TestCase;

/**
 * @covers PharIo\Manifest\Url
 */
class UrlTest extends TestCase {
    public function testCanBeCreatedForValidUrl() {
        $this->assertInstanceOf(Url::class, new Url('https://phar.io/'));
    }

    public function testCanBeUsedAsString() {
        $this->assertEquals('https://phar.io/', new Url('https://phar.io/'));
    }

    /**
     * @covers PharIo\Manifest\InvalidUrlException
     */
    public function testCannotBeCreatedForInvalidUrl() {
        $this->expectException(InvalidUrlException::class);

        new Url('invalid');
    }
}
