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
 * @covers \PharIo\Version\Version
 */
class VersionTest extends TestCase {
    /**
     * @dataProvider versionProvider
     *
     * @param string $versionString
     * @param string $expectedMajor
     * @param string $expectedMinor
     * @param string $expectedPatch
     * @param string $expectedPreReleaseValue
     * @param int $expectedReleaseCount
     */
    public function testParsesVersionNumbers(
        $versionString,
        $expectedMajor,
        $expectedMinor,
        $expectedPatch,
        $expectedPreReleaseValue = '',
        $expectedReleaseCount = 0
    ) {
        $version = new Version($versionString);

        $this->assertSame($expectedMajor, $version->getMajor()->getValue());
        $this->assertSame($expectedMinor, $version->getMinor()->getValue());
        $this->assertSame($expectedPatch, $version->getPatch()->getValue());
        if ($expectedPreReleaseValue !== '') {
            $this->assertSame($expectedPreReleaseValue, $version->getPreReleaseSuffix()->getValue());
        }
        if ($expectedReleaseCount !== 0) {
            $this->assertSame($expectedReleaseCount, $version->getPreReleaseSuffix()->getNumber());
        }

        $this->assertSame($versionString, $version->getVersionString());
    }

    public function versionProvider() {
        return [
            ['0.0.1', '0', '0', '1'],
            ['0.1.2', '0', '1', '2'],
            ['1.0.0-alpha', '1', '0', '0', 'alpha'],
            ['3.4.12-dev3', '3', '4', '12', 'dev', 3],
        ];
    }

    /**
     * @dataProvider versionGreaterThanProvider
     *
     * @param Version $versionA
     * @param Version $versionB
     * @param bool $expectedResult
     */
    public function testIsGreaterThan(Version $versionA, Version $versionB, $expectedResult) {
        $this->assertSame($expectedResult, $versionA->isGreaterThan($versionB));
    }

    /**
     * @return array
     */
    public function versionGreaterThanProvider() {
        return [
            [new Version('1.0.0'), new Version('1.0.1'), false],
            [new Version('1.0.1'), new Version('1.0.0'), true],
            [new Version('1.1.0'), new Version('1.0.1'), true],
            [new Version('1.1.0'), new Version('2.0.1'), false],
            [new Version('1.1.0'), new Version('1.1.0'), false],
            [new Version('2.5.8'), new Version('1.6.8'), true],
            [new Version('2.5.8'), new Version('2.6.8'), false],
            [new Version('2.5.8'), new Version('3.1.2'), false],
            [new Version('3.0.0-alpha1'), new Version('3.0.0-alpha2'), false],
            [new Version('3.0.0-alpha2'), new Version('3.0.0-alpha1'), true],
            [new Version('3.0.0-alpha.1'), new Version('3.0.0'), false],
            [new Version('3.0.0'), new Version('3.0.0-alpha.1'), true],
        ];
    }

    /**
     * @dataProvider invalidVersionStringProvider
     *
     * @param string $versionString
     */
    public function testThrowsExceptionIfVersionStringDoesNotFollowSemVer($versionString) {
        $this->expectException(InvalidVersionException::class);
        new Version($versionString);
    }

    /**
     * @return array
     */
    public function invalidVersionStringProvider() {
        return [
            ['foo'],
            ['0.0.1-dev+ABC', '0', '0', '1', 'dev', 'ABC'],
            ['1.0.0-x.7.z.92', '1', '0', '0', 'x.7.z.92']
        ];
    }

}
