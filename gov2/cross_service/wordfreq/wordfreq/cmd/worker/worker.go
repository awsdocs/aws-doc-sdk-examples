// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"bufio"
	"fmt"
	"io"
	"sort"
	"strings"

	wordfreq "example.aws/go-v2/examples/cross_service/wordfreq/service/shared"
)

// countTopWords counts the top words returning those words or error.
func countTopWords(reader io.Reader, top int) ([]wordfreq.Word, error) {
	wordMap, err := countWords(reader)
	if err != nil {
		return nil, err
	}

	words := collectTopWords(wordMap, top)

	return words, nil
}

// countWords collects the counts of all words received from an io.Reader. Using
// a word scanner, every word gets counted. This is a fairly simplistic implementation
// of word counting and only splits words based on whitespace. Extra characters
// such as `.,"'?!` are trimmed from the front and end of each string
func countWords(reader io.Reader) (map[string]int, error) {
	wordMap := map[string]int{}

	scanner := bufio.NewScanner(reader)
	scanner.Split(bufio.ScanWords)
	for scanner.Scan() {
		word := strings.ToLower(scanner.Text())
		if len(word) <= 4 {
			continue
		}
		word = strings.Trim(word, `.,"'?!`)

		curCount := 0
		if v, ok := wordMap[word]; ok {
			curCount = v
		}

		wordMap[word] = 1 + curCount

	}
	if err := scanner.Err(); err != nil {
		return nil, fmt.Errorf("failed to count words, %v", err)
	}

	return wordMap, nil
}

// collectTopWords converts the word map into an array, and sorts it. Collecting
// the top words.
func collectTopWords(wordMap map[string]int, top int) wordfreq.Words {
	words := wordfreq.Words{}
	for word, count := range wordMap {
		words = append(words, wordfreq.Word{Word: word, Count: count})
	}
	sort.Sort(words)

	if top >= len(words) {
		return words
	}
	return words[:top]
}
