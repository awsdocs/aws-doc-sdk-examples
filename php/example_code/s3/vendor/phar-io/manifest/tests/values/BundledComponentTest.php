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

use PharIo\Version\Version;
use PHPUnit\Framework\TestCase;

/**
 * @covers PharIo\Manifest\BundledComponent
 *
 * @uses \PharIo\Version\Version
 */
class BundledComponentTest extends TestCase {
    /**
     * @var BundledComponent
     */
    private $bundledComponent;

    protected function setUp() {
        $this->bundledComponent = new BundledComponent('phpunit/php-code-coverage', new Version('4.0.2'));
    }

    public function testCanBeCreated() {
        $this->assertInstanceOf(BundledComponent::class, $this->bundledComponent);
    }

    public function testNameCanBeRetrieved() {
        $this->assertEquals('phpunit/php-code-coverage', $this->bundledComponent->getName());
    }

    public function testVersionCanBeRetrieved() {
        $this->assertEquals('4.0.2', $this->bundledComponent->getVersion()->getVersionString());
    }
}
