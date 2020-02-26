<?php declare(strict_types=1);
/*
 * This file is part of sebastian/global-state.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\GlobalState\TestFixture;

class SnapshotClass
{
    private static $string = 'string';

    private static $closures = [];

    private static $files = [];

    private static $resources = [];

    private static $objects = [];

    public static function init(): void
    {
        self::$closures[] = function (): void {
        };

        self::$files[] = new \SplFileInfo(__FILE__);

        self::$resources[] = \fopen('php://memory', 'r');

        self::$objects[] = new \stdClass;
    }
}
