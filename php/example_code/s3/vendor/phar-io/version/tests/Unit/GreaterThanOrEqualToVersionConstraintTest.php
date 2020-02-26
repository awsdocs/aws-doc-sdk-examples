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
 * @covers \PharIo\Version\GreaterThanOrEqualToVersionConstraint
 */
class GreaterThanOrEqualToVersionConstraintTest extends TestCase {
    public function versionProvider() {
        return [
            // compliant versions
            [new Version('1.0.2'), new Version('1.0.2'), true],
            [new Version('1.0.2'), new Version('1.0.3'), true],
            [new Version('1.0.2'), new Version('1.1.1'), true],
            [new Version('1.0.2'), new Version('2.0.0'), true],
            [new Version('1.0.2'), new Version('1.0.3'), true],
            // non-compliant versions
            [new Version('1.0.2'), new Version('1.0.1'), false],
            [new Version('1.9.8'), new Version('0.9.9'), false],
            [new Version('2.3.1'), new Version('2.2.3'), false],
            [new Version('3.0.2'), new Version('2.9.9'), false],
        ];
    }

    /**
     * @dataProvider versionProvider
     *
     * @param Version $constraintVersion
     * @param Version $version
     * @param bool $expectedResult
     */
    public function testReturnsTrueForCompliantVersions(Version $constraintVersion, Version $version, $expectedResult) {
        $constraint = new GreaterThanOrEqualToVersionConstraint('foo', $constraintVersion);

        $this->assertSame($expectedResult, $constraint->complies($version));
    }
}
