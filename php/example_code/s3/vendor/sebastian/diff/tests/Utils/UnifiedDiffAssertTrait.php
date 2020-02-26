<?php declare(strict_types=1);
/*
 * This file is part of sebastian/diff.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace SebastianBergmann\Diff\Utils;

trait UnifiedDiffAssertTrait
{
    /**
     * @param string $diff
     *
     * @throws \UnexpectedValueException
     */
    public function assertValidUnifiedDiffFormat(string $diff): void
    {
        if ('' === $diff) {
            $this->addToAssertionCount(1);

            return;
        }

        // test diff ends with a line break
        $last = \substr($diff, -1);

        if ("\n" !== $last && "\r" !== $last) {
            throw new \UnexpectedValueException(\sprintf('Expected diff to end with a line break, got "%s".', $last));
        }

        $lines            = \preg_split('/(.*\R)/', $diff, -1, PREG_SPLIT_DELIM_CAPTURE | PREG_SPLIT_NO_EMPTY);
        $lineCount        = \count($lines);
        $lineNumber       = $diffLineFromNumber       = $diffLineToNumber       = 1;
        $fromStart        = $fromTillOffset        = $toStart        = $toTillOffset        = -1;
        $expectHunkHeader = true;

        // check for header
        if ($lineCount > 1) {
            $this->unifiedDiffAssertLinePrefix($lines[0], 'Line 1.');
            $this->unifiedDiffAssertLinePrefix($lines[1], 'Line 2.');

            if ('---' === \substr($lines[0], 0, 3)) {
                if ('+++' !== \substr($lines[1], 0, 3)) {
                    throw new \UnexpectedValueException(\sprintf("Line 1 indicates a header, so line 2 must start with \"+++\".\nLine 1: \"%s\"\nLine 2: \"%s\".", $lines[0], $lines[1]));
                }

                $this->unifiedDiffAssertHeaderLine($lines[0], '--- ', 'Line 1.');
                $this->unifiedDiffAssertHeaderLine($lines[1], '+++ ', 'Line 2.');

                $lineNumber = 3;
            }
        }

        $endOfLineTypes = [];
        $diffClosed     = false;

        // assert format of lines, get all hunks, test the line numbers
        for (; $lineNumber <= $lineCount; ++$lineNumber) {
            if ($diffClosed) {
                throw new \UnexpectedValueException(\sprintf('Unexpected line as 2 "No newline" markers have found, ". Line %d.', $lineNumber));
            }

            $line = $lines[$lineNumber - 1]; // line numbers start by 1, array index at 0
            $type = $this->unifiedDiffAssertLinePrefix($line, \sprintf('Line %d.', $lineNumber));

            if ($expectHunkHeader && '@' !== $type && '\\' !== $type) {
                throw new \UnexpectedValueException(\sprintf('Expected hunk start (\'@\'), got "%s". Line %d.', $type, $lineNumber));
            }

            if ('@' === $type) {
                if (!$expectHunkHeader) {
                    throw new \UnexpectedValueException(\sprintf('Unexpected hunk start (\'@\'). Line %d.', $lineNumber));
                }

                $previousHunkFromEnd = $fromStart + $fromTillOffset;
                $previousHunkTillEnd = $toStart + $toTillOffset;

                [$fromStart, $fromTillOffset, $toStart, $toTillOffset] = $this->unifiedDiffAssertHunkHeader($line, \sprintf('Line %d.', $lineNumber));

                // detect overlapping hunks
                if ($fromStart < $previousHunkFromEnd) {
                    throw new \UnexpectedValueException(\sprintf('Unexpected new hunk; "from" (\'-\') start overlaps previous hunk. Line %d.', $lineNumber));
                }

                if ($toStart < $previousHunkTillEnd) {
                    throw new \UnexpectedValueException(\sprintf('Unexpected new hunk; "to" (\'+\') start overlaps previous hunk. Line %d.', $lineNumber));
                }

                /* valid states; hunks touches against each other:
                    $fromStart === $previousHunkFromEnd
                    $toStart === $previousHunkTillEnd
                */

                $diffLineFromNumber = $fromStart;
                $diffLineToNumber   = $toStart;
                $expectHunkHeader   = false;

                continue;
            }

            if ('-' === $type) {
                if (isset($endOfLineTypes['-'])) {
                    throw new \UnexpectedValueException(\sprintf('Not expected from (\'-\'), already closed by "\\ No newline at end of file". Line %d.', $lineNumber));
                }

                ++$diffLineFromNumber;
            } elseif ('+' === $type) {
                if (isset($endOfLineTypes['+'])) {
                    throw new \UnexpectedValueException(\sprintf('Not expected to (\'+\'), already closed by "\\ No newline at end of file". Line %d.', $lineNumber));
                }

                ++$diffLineToNumber;
            } elseif (' ' === $type) {
                if (isset($endOfLineTypes['-'])) {
                    throw new \UnexpectedValueException(\sprintf('Not expected same (\' \'), \'-\' already closed by "\\ No newline at end of file". Line %d.', $lineNumber));
                }

                if (isset($endOfLineTypes['+'])) {
                    throw new \UnexpectedValueException(\sprintf('Not expected same (\' \'), \'+\' already closed by "\\ No newline at end of file". Line %d.', $lineNumber));
                }

                ++$diffLineFromNumber;
                ++$diffLineToNumber;
            } elseif ('\\' === $type) {
                if (!isset($lines[$lineNumber - 2])) {
                    throw new \UnexpectedValueException(\sprintf('Unexpected "\\ No newline at end of file", it must be preceded by \'+\' or \'-\' line. Line %d.', $lineNumber));
                }

                $previousType = $this->unifiedDiffAssertLinePrefix($lines[$lineNumber - 2], \sprintf('Preceding line of "\\ No newline at end of file" of unexpected format. Line %d.', $lineNumber));

                if (isset($endOfLineTypes[$previousType])) {
                    throw new \UnexpectedValueException(\sprintf('Unexpected "\\ No newline at end of file", "%s" was already closed. Line %d.', $type, $lineNumber));
                }

                $endOfLineTypes[$previousType] = true;
                $diffClosed                    = \count($endOfLineTypes) > 1;
            } else {
                // internal state error
                throw new \RuntimeException(\sprintf('Unexpected line type "%s" Line %d.', $type, $lineNumber));
            }

            $expectHunkHeader =
                $diffLineFromNumber === ($fromStart + $fromTillOffset)
                && $diffLineToNumber === ($toStart + $toTillOffset)
            ;
        }

        if (
            $diffLineFromNumber !== ($fromStart + $fromTillOffset)
            && $diffLineToNumber !== ($toStart + $toTillOffset)
        ) {
            throw new \UnexpectedValueException(\sprintf('Unexpected EOF, number of lines in hunk "from" (\'-\')) and "to" (\'+\') mismatched. Line %d.', $lineNumber));
        }

        if ($diffLineFromNumber !== ($fromStart + $fromTillOffset)) {
            throw new \UnexpectedValueException(\sprintf('Unexpected EOF, number of lines in hunk "from" (\'-\')) mismatched. Line %d.', $lineNumber));
        }

        if ($diffLineToNumber !== ($toStart + $toTillOffset)) {
            throw new \UnexpectedValueException(\sprintf('Unexpected EOF, number of lines in hunk "to" (\'+\')) mismatched. Line %d.', $lineNumber));
        }

        $this->addToAssertionCount(1);
    }

    /**
     * @param string $line
     * @param string $message
     *
     * @return string '+', '-', '@', ' ' or '\'
     */
    private function unifiedDiffAssertLinePrefix(string $line, string $message): string
    {
        $this->unifiedDiffAssertStrLength($line, 2, $message); // 2: line type indicator ('+', '-', ' ' or '\') and a line break
        $firstChar = $line[0];

        if ('+' === $firstChar || '-' === $firstChar || '@' === $firstChar || ' ' === $firstChar) {
            return $firstChar;
        }

        if ("\\ No newline at end of file\n" === $line) {
            return '\\';
        }

        throw new \UnexpectedValueException(\sprintf('Expected line to start with \'@\', \'-\' or \'+\', got "%s". %s', $line, $message));
    }

    private function unifiedDiffAssertStrLength(string $line, int $min, string $message): void
    {
        $length = \strlen($line);

        if ($length < $min) {
            throw new \UnexpectedValueException(\sprintf('Expected string length of minimal %d, got %d. %s', $min, $length, $message));
        }
    }

    /**
     * Assert valid unified diff header line
     *
     * Samples:
     * - "+++ from1.txt\t2017-08-24 19:51:29.383985722 +0200"
     * - "+++ from1.txt"
     *
     * @param string $line
     * @param string $start
     * @param string $message
     */
    private function unifiedDiffAssertHeaderLine(string $line, string $start, string $message): void
    {
        if (0 !== \strpos($line, $start)) {
            throw new \UnexpectedValueException(\sprintf('Expected header line to start with "%s", got "%s". %s', $start . ' ', $line, $message));
        }

        // sample "+++ from1.txt\t2017-08-24 19:51:29.383985722 +0200\n"
        $match = \preg_match(
            "/^([^\t]*)(?:[\t]([\\S].*[\\S]))?\n$/",
            \substr($line, 4), // 4 === string length of "+++ " / "--- "
            $matches
        );

        if (1 !== $match) {
            throw new \UnexpectedValueException(\sprintf('Header line does not match expected pattern, got "%s". %s', $line, $message));
        }

        // $file = $matches[1];

        if (\count($matches) > 2) {
            $this->unifiedDiffAssertHeaderDate($matches[2], $message);
        }
    }

    private function unifiedDiffAssertHeaderDate(string $date, string $message): void
    {
        // sample "2017-08-24 19:51:29.383985722 +0200"
        $match = \preg_match(
            '/^([\d]{4})-([01]?[\d])-([0123]?[\d])(:? [\d]{1,2}:[\d]{1,2}(?::[\d]{1,2}(:?\.[\d]+)?)?(?: ([\+\-][\d]{4}))?)?$/',
            $date,
            $matches
        );

        if (1 !== $match || ($matchesCount = \count($matches)) < 4) {
            throw new \UnexpectedValueException(\sprintf('Date of header line does not match expected pattern, got "%s". %s', $date, $message));
        }

        // [$full, $year, $month, $day, $time] = $matches;
    }

    /**
     * @param string $line
     * @param string $message
     *
     * @return int[]
     */
    private function unifiedDiffAssertHunkHeader(string $line, string $message): array
    {
        if (1 !== \preg_match('#^@@ -([\d]+)((?:,[\d]+)?) \+([\d]+)((?:,[\d]+)?) @@\n$#', $line, $matches)) {
            throw new \UnexpectedValueException(
                \sprintf(
                    'Hunk header line does not match expected pattern, got "%s". %s',
                    $line,
                    $message
                )
            );
        }

        return [
            (int) $matches[1],
            empty($matches[2]) ? 1 : (int) \substr($matches[2], 1),
            (int) $matches[3],
            empty($matches[4]) ? 1 : (int) \substr($matches[4], 1),
        ];
    }
}
