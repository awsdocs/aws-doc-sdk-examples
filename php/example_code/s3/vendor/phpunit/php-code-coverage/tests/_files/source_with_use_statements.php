<?php
namespace SebastianBergmann\CodeCoverage\TestFixture;

use stdClass;
use function array_filter;
use const ARRAY_FILTER_USE_BOTH;

class C
{
    public function m(): void
    {
        $o = new stdClass;

        array_filter(
            ['a' => 1, 'b' => 2, 'c' => 3, 'd' => 4],
            static function ($v, $k)
            {
                return $k === 'b' || $v === 4;
            },
            ARRAY_FILTER_USE_BOTH
        );
    }
}
