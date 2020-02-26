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
 * @covers \PharIo\Version\VersionConstraintParser
 */
class VersionConstraintParserTest extends TestCase {
    /**
     * @dataProvider versionStringProvider
     *
     * @param string $versionString
     * @param VersionConstraint $expectedConstraint
     */
    public function testReturnsExpectedConstraint($versionString, VersionConstraint $expectedConstraint) {
        $parser = new VersionConstraintParser;

        $this->assertEquals($expectedConstraint, $parser->parse($versionString));
    }

    /**
     * @dataProvider unsupportedVersionStringProvider
     *
     * @param string $versionString
     */
    public function testThrowsExceptionIfVersionStringIsNotSupported($versionString) {
        $parser = new VersionConstraintParser;

        $this->expectException(UnsupportedVersionConstraintException::class);

        $parser->parse($versionString);
    }

    /**
     * @return array
     */
    public function versionStringProvider() {
        return [
            ['1.0.2', new ExactVersionConstraint('1.0.2')],
            [
                '~4.6',
                new AndVersionConstraintGroup(
                    '~4.6',
                    [
                        new GreaterThanOrEqualToVersionConstraint('~4.6', new Version('4.6')),
                        new SpecificMajorVersionConstraint('~4.6', 4)
                    ]
                )
            ],
            [
                '~4.6.2',
                new AndVersionConstraintGroup(
                    '~4.6.2',
                    [
                        new GreaterThanOrEqualToVersionConstraint('~4.6.2', new Version('4.6.2')),
                        new SpecificMajorAndMinorVersionConstraint('~4.6.2', 4, 6)
                    ]
                )
            ],
            [
                '^2.6.1',
                new AndVersionConstraintGroup(
                    '^2.6.1',
                    [
                        new GreaterThanOrEqualToVersionConstraint('^2.6.1', new Version('2.6.1')),
                        new SpecificMajorVersionConstraint('^2.6.1', 2)
                    ]
                )
            ],
            ['5.1.*', new SpecificMajorAndMinorVersionConstraint('5.1.*', 5, 1)],
            ['5.*', new SpecificMajorVersionConstraint('5.*', 5)],
            ['*', new AnyVersionConstraint()],
            [
                '1.0.2 || 1.0.5',
                new OrVersionConstraintGroup(
                    '1.0.2 || 1.0.5',
                    [
                        new ExactVersionConstraint('1.0.2'),
                        new ExactVersionConstraint('1.0.5')
                    ]
                )
            ],
            [
                '^5.6 || ^7.0',
                new OrVersionConstraintGroup(
                    '^5.6 || ^7.0',
                    [
                        new AndVersionConstraintGroup(
                            '^5.6', [
                                new GreaterThanOrEqualToVersionConstraint('^5.6', new Version('5.6')),
                                new SpecificMajorVersionConstraint('^5.6', 5)
                            ]
                        ),
                        new AndVersionConstraintGroup(
                            '^7.0', [
                                new GreaterThanOrEqualToVersionConstraint('^7.0', new Version('7.0')),
                                new SpecificMajorVersionConstraint('^7.0', 7)
                            ]
                        )
                    ]
                )
            ],
            ['7.0.28-1', new ExactVersionConstraint('7.0.28-1')],
            [
                '^3.0.0-alpha1',
                new AndVersionConstraintGroup(
                    '^3.0.0-alpha1',
                    [
                        new GreaterThanOrEqualToVersionConstraint('^3.0.0-alpha1', new Version('3.0.0-alpha1')),
                        new SpecificMajorVersionConstraint('^3.0.0-alpha1', 3)
                    ]
                )
            ],
            [
                '^3.0.0-alpha.1',
                new AndVersionConstraintGroup(
                    '^3.0.0-alpha.1',
                    [
                        new GreaterThanOrEqualToVersionConstraint('^3.0.0-alpha.1', new Version('3.0.0-alpha.1')),
                        new SpecificMajorVersionConstraint('^3.0.0-alpha.1', 3)
                    ]
                )
            ]
        ];
    }

    public function unsupportedVersionStringProvider() {
        return [
            ['foo'],
            ['+1.0.2'],
            ['>=2.0'],
            ['^5.6 || >= 7.0'],
            ['2.0 || foo']
        ];
    }
}
