package io.inbot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        public Stream<String> strings() {
            if(isLeaf()) {
                return Stream.empty();
            } else {
                return children.entrySet().stream().flatMap(entry -> {
                    TrieNode n = entry.getValue();
                    if(n.isLeaf()) {
                        return Stream.of(""+ entry.getKey());
                    } else {
                        return n.strings().map(s -> "" + entry.getKey() +s);
                    }
                });
            }
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

   public List<String> match(String input) {
       TrieNode currentNode = root;
       int i=0;
       List<String> results = new ArrayList<>();
       for(char c: input.toCharArray()) {
           TrieNode nextNode = currentNode.getChildren().get(c);
           if(nextNode != null) {
               i++;
               currentNode=nextNode;
           }
       }
       if(i>0 && currentNode.isLeaf()) {
           results.add(input); // fully matched against something
       } else if(!currentNode.equals(root) && i == input.length()) {
           String matchedPrefix=input.substring(0,i);
           List<String> matches = currentNode.strings().map(s -> matchedPrefix + s).collect(Collectors.toList());
        return matches;
       }
       return results;

   }
}