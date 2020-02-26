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
 * @covers \PharIo\Version\ExactVersionConstraint
 */
class ExactVersionConstraintTest extends TestCase {
    public function compliantVersionProvider() {
        return [
            ['1.0.2', new Version('1.0.2')],
            ['4.8.9', new Version('4.8.9')],
            ['4.8', new Version('4.8')],
        ];
    }

    public function nonCompliantVersionProvider() {
        return [
            ['1.0.2', new Version('1.0.3')],
            ['4.8.9', new Version('4.7.9')],
            ['4.8', new Version('4.8.5')],
        ];
    }

    /**
     * @dataProvider compliantVersionProvider
     *
     * @param string $constraintValue
     * @param Version $version
     */
    public function testReturnsTrueForCompliantVersion($constraintValue, Version $version) {
        $constraint = new ExactVersionConstraint($constraintValue);

        $this->assertTrue($constraint->complies($version));
    }

    /**
     * @dataProvider nonCompliantVersionProvider
     *
     * @param string $constraintValue
     * @param Version $version
     */
    public function testReturnsFalseForNonCompliantVersion($constraintValue, Version $version) {
        $constraint = new ExactVersionConstraint($constraintValue);

        $this->assertFalse($constraint->complies($version));
    }
}
