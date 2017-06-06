package io.inbot.utils;

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.util.List;
import org.testng.annotations.Test;

@Test
public class SimpleStringTrieTest {
    public void shouldTriePrefixes() {
        SimpleStringTrie trie = new SimpleStringTrie();
        // these prefixes we want to find
        trie.add("foofo");
        trie.add("foofoo");
        trie.add("fooboooooo");
        trie.add("1234");
        trie.add("123");
        // these inputs have no prefix that was added
        assertThat(trie.get("foo").isPresent()).isFalse();
        assertThat(trie.get("foobar").isPresent()).isFalse();
        assertThat(trie.get("abc").isPresent()).isFalse();
        assertThat(trie.get("1200").isPresent()).isFalse();
        // these inputs have matching prefixes that were added
        assertThat(trie.get("foofo").get()).isEqualTo("foofo");
        assertThat(trie.get("foofoxxxxxxxxxx").get()).isEqualTo("foofo");
        assertThat(trie.get("foofoo").get()).isEqualTo("foofoo");
        assertThat(trie.get("foofoooo").get()).isEqualTo("foofoo");
        assertThat(trie.get("fooboooooo").get()).isEqualTo("fooboooooo");
        assertThat(trie.get("1234").get()).isEqualTo("1234");
        assertThat(trie.get("1230").get()).isEqualTo("123");
    }

    public void shouldMatch() {
        SimpleStringTrie trie = new SimpleStringTrie();
        trie.add("foofoo");
        trie.add("foobar");
        trie.add("bar");

        List<String> match = trie.match("fo");

        assertThat(match.contains("foofoo")).isTrue();
        assertThat(match.contains("foobar")).isTrue();
        assertThat(match.contains("bar")).isFalse();

    }
}
