<?php
/*
 * This file is part of PharIo\Version.
 *
 * (c) Arne Blankerts <arne@blankerts.de>, Sebastian Heuer <sebastian@phpeople.de>, Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace PharIo\Version;

use PHPUnit\Framework\TestCase;

/**
 * @covers \PharIo\Version\AnyVersionConstraint
 */
class AnyVersionConstraintTest extends TestCase {
    public function versionProvider() {
        return [
            [new Version('1.0.2')],
            [new Version('4.8')],
            [new Version('0.1.1-dev')]
        ];
    }

    /**
     * @dataProvider versionProvider
     *
     * @param Version $version
     */
    public function testReturnsTrue(Version $version) {
        $constraint = new AnyVersionConstraint;

        $this->assertTrue($constraint->complies($version));
    }

    public function testAsString() {
        $this->assertSame('*', (new AnyVersionConstraint())->asString());
    }
}
