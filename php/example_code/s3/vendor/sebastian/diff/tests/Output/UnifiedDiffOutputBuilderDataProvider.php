<?php declare(strict_types=1);
/*
 * This file is part of sebastian/diff.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace SebastianBergmann\Diff\Output;

final class UnifiedDiffOutputBuilderDataProvider
{
    public static function provideDiffWithLineNumbers(): array
    {
        return [
            'diff line 1 non_patch_compat' => [
'--- Original
+++ New
@@ -1 +1 @@
-AA
+BA
',
                'AA',
                'BA',
            ],
            'diff line +1 non_patch_compat' => [
'--- Original
+++ New
@@ -1 +1,2 @@
-AZ
+
+B
',
                'AZ',
                "\nB",
            ],
            'diff line -1 non_patch_compat' => [
'--- Original
+++ New
@@ -1,2 +1 @@
-
-AF
+B
',
                "\nAF",
                'B',
            ],
            'II non_patch_compat' => [
'--- Original
+++ New
@@ -1,4 +1,2 @@
-
-
 A
 1
',
                "\n\nA\n1",
                "A\n1",
            ],
            'diff last line II - no trailing linebreak non_patch_compat' => [
'--- Original
+++ New
@@ -5,4 +5,4 @@
 ' . '
 ' . '
 ' . '
-E
+B
',
                "A\n\n\n\n\n\n\nE",
                "A\n\n\n\n\n\n\nB",
            ],
            [
                "--- Original\n+++ New\n@@ -1,2 +1 @@\n \n-\n",
                "\n\n",
                "\n",
            ],
            'diff line endings non_patch_compat' => [
                "--- Original\n+++ New\n@@ -1 +1 @@\n #Warning: Strings contain different line endings!\n-<?php\r\n+<?php\n",
                "<?php\r\n",
                "<?php\n",
            ],
        'same non_patch_compat' => [
'--- Original
+++ New
',
                "AT\n",
                "AT\n",
            ],
            [
'--- Original
+++ New
@@ -1,4 +1,4 @@
-b
+a
 ' . '
 ' . '
 ' . '
',
                "b\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n",
                "a\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n",
            ],
            'diff line @1' => [
'--- Original
+++ New
@@ -1,2 +1,2 @@
 ' . '
-AG
+B
',
                "\nAG\n",
                "\nB\n",
            ],
            'same multiple lines' => [
'--- Original
+++ New
@@ -1,4 +1,4 @@
 ' . '
 ' . '
-V
+B
 C213
',
                "\n\nV\nC213",
                "\n\nB\nC213",
            ],
            'diff last line I' => [
'--- Original
+++ New
@@ -5,4 +5,4 @@
 ' . '
 ' . '
 ' . '
-E
+B
',
                "A\n\n\n\n\n\n\nE\n",
                "A\n\n\n\n\n\n\nB\n",
            ],
            'diff line middle' => [
'--- Original
+++ New
@@ -5,7 +5,7 @@
 ' . '
 ' . '
 ' . '
-X
+Z
 ' . '
 ' . '
 ' . '
',
                "A\n\n\n\n\n\n\nX\n\n\n\n\n\n\nAY",
                "A\n\n\n\n\n\n\nZ\n\n\n\n\n\n\nAY",
            ],
            'diff last line III' => [
'--- Original
+++ New
@@ -12,4 +12,4 @@
 ' . '
 ' . '
 ' . '
-A
+B
',
                "A\n\n\n\n\n\n\nA\n\n\n\n\n\n\nA\n",
                "A\n\n\n\n\n\n\nA\n\n\n\n\n\n\nB\n",
            ],
            [
'--- Original
+++ New
@@ -1,8 +1,8 @@
 A
-B
+B1
 D
 E
 EE
 F
-G
+G1
 H
',
                "A\nB\nD\nE\nEE\nF\nG\nH",
                "A\nB1\nD\nE\nEE\nF\nG1\nH",
            ],
            [
'--- Original
+++ New
@@ -1,4 +1,5 @@
 Z
+
 a
 b
 c
@@ -7,5 +8,5 @@
 f
 g
 h
-i
+x
 j
',
'Z
a
b
c
d
e
f
g
h
i
j
',
'Z

a
b
c
d
e
f
g
h
x
j
',
            ],
            [
'--- Original
+++ New
@@ -1,7 +1,5 @@
-
-a
+b
 A
-X
-
+Y
 ' . '
 A
',
                "\na\nA\nX\n\n\nA\n",
                "b\nA\nY\n\nA\n",
            ],
            [
<<<EOF
--- Original
+++ New
@@ -1,7 +1,5 @@
-
-
 a
-b
+p
 c
 d
 e
@@ -9,5 +7,5 @@
 g
 h
 i
-j
+w
 k

EOF
                ,
                "\n\na\nb\nc\nd\ne\nf\ng\nh\ni\nj\nk\n",
                "a\np\nc\nd\ne\nf\ng\nh\ni\nw\nk\n",
            ],
            [
'--- Original
+++ New
@@ -8,7 +8,7 @@
 ' . '
 ' . '
 ' . '
-A
+C
 ' . '
 ' . '
 ' . '
',
                "E\n\n\n\n\nB\n\n\n\n\nA\n\n\n\n\n\n\n\n\nD1",
                "E\n\n\n\n\nB\n\n\n\n\nC\n\n\n\n\n\n\n\n\nD1",
            ],
            [
'--- Original
+++ New
@@ -5,7 +5,7 @@
 ' . '
 ' . '
 ' . '
-Z
+U
 ' . '
 ' . '
 ' . '
@@ -12,7 +12,7 @@
 ' . '
 ' . '
 ' . '
-X
+V
 ' . '
 ' . '
 ' . '
@@ -19,7 +19,7 @@
 ' . '
 ' . '
 ' . '
-Y
+W
 ' . '
 ' . '
 ' . '
@@ -26,7 +26,7 @@
 ' . '
 ' . '
 ' . '
-W
+X
 ' . '
 ' . '
 ' . '
@@ -33,7 +33,7 @@
 ' . '
 ' . '
 ' . '
-V
+Y
 ' . '
 ' . '
 ' . '
@@ -40,4 +40,4 @@
 ' . '
 ' . '
 ' . '
-U
+Z
',
                "\n\n\n\n\n\n\nZ\n\n\n\n\n\n\nX\n\n\n\n\n\n\nY\n\n\n\n\n\n\nW\n\n\n\n\n\n\nV\n\n\n\n\n\n\nU\n",
                "\n\n\n\n\n\n\nU\n\n\n\n\n\n\nV\n\n\n\n\n\n\nW\n\n\n\n\n\n\nX\n\n\n\n\n\n\nY\n\n\n\n\n\n\nZ\n",
            ],
            [
<<<EOF
--- Original
+++ New
@@ -1,5 +1,5 @@
 a
-b
+p
 c
 d
 e
@@ -7,5 +7,5 @@
 g
 h
 i
-j
+w
 k

EOF
                ,
                "a\nb\nc\nd\ne\nf\ng\nh\ni\nj\nk\n",
                "a\np\nc\nd\ne\nf\ng\nh\ni\nw\nk\n",
            ],
            [
<<<EOF
--- Original
+++ New
@@ -1,4 +1,4 @@
-A
+B
 1
 2
 3

EOF
                ,
                "A\n1\n2\n3\n4\n5\n6\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1",
                "B\n1\n2\n3\n4\n5\n6\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1",
            ],
            [
                "--- Original\n+++ New\n@@ -4,7 +4,7 @@\n D\n E\n F\n-X\n+Y\n G\n H\n I\n",
                "A\nB\nC\nD\nE\nF\nX\nG\nH\nI\nJ\nK\nL\nM\n",
                "A\nB\nC\nD\nE\nF\nY\nG\nH\nI\nJ\nK\nL\nM\n",
            ],
        ];
    }
}
