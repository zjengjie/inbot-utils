package io.inbot.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Simple implementation of a Trie that may be used to match input strings to the longest matching prefix.
 */
public class SimpleStringTrie {
    private final TrieNode root;

    private static class TrieNode {
        private final Map<Character, TrieNode> children;
        boolean end=false;

        public TrieNode(char ch) {
            children = new HashMap<>();
        }

        public Map<Character, TrieNode> getChildren() {
            return children;
        }

        public boolean isLeaf() {
            return end;
        }
    }

    public SimpleStringTrie() {
        root = new TrieNode((char) 0);
    }

    /**
     * Useful if you want to build a trie for an existing map so you can figure out a matching prefix that has an entry
     * @param map a map
     * @return a SimpleStringTrie for the map.
     */
    public static SimpleStringTrie from(Map<String,?> map) {
        SimpleStringTrie st = new SimpleStringTrie();
        map.keySet().forEach(key -> st.add(key));
        return st;
    }

    /**
     * Add a string to the trie.
     * @param input any String
     */
    public void add(String input) {
        TrieNode currentNode = root;

        for(char c: input.toCharArray()) {
            Map<Character, TrieNode> children = currentNode.getChildren();
            TrieNode matchingNode = children.get(c);
            if(matchingNode != null) {
                currentNode = matchingNode;
            } else {
                TrieNode newNode = new TrieNode(c);
                children.put(c, newNode);
                currentNode = newNode;
            }
        }
        currentNode.end=true;
    }

    /**
     * Return the longest matching prefix of the input string that was added to the trie.
     * @param input a string
     * @return Optional of longest matching prefix that was added to the trie
     */
    public Optional<String> get(String input) {
        TrieNode currentNode = root;
        int i=0;
        for(char c: input.toCharArray()) {
            TrieNode nextNode = currentNode.getChildren().get(c);
            if(nextNode != null) {
                i++;
                currentNode=nextNode;
            } else {
                if(i>0 && currentNode.isLeaf()) {
                    return Optional.of(input.substring(0, i));
                }
            }
        }
        if(i>0 && currentNode.isLeaf()) {
            return Optional.of(input.substring(0, i));
        }
        return Optional.empty();
   }
}