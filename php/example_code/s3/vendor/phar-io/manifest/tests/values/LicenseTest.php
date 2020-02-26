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
 * @covers PharIo\Manifest\License
 *
 * @uses PharIo\Manifest\Url
 */
class LicenseTest extends TestCase {
    /**
     * @var License
     */
    private $license;

    protected function setUp() {
        $this->license = new License('BSD-3-Clause', new Url('https://github.com/sebastianbergmann/phpunit/blob/master/LICENSE'));
    }

    public function testCanBeCreated() {
        $this->assertInstanceOf(License::class, $this->license);
    }

    public function testNameCanBeRetrieved() {
        $this->assertEquals('BSD-3-Clause', $this->license->getName());
    }

    public function testUrlCanBeRetrieved() {
        $this->assertEquals('https://github.com/sebastianbergmann/phpunit/blob/master/LICENSE', $this->license->getUrl());
    }
}
