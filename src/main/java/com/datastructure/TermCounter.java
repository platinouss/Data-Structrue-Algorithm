package com.datastructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class TermCounter {

    private String label;   // url 저장
    private Map<String, Integer> map;   //<검색어, 검색 횟수>

    public TermCounter(String label) {
        this.label = label;
        this.map = new HashMap<String, Integer>();
    }

    public String getLabel() {
        return label;
    }

    public int size() {
        int size = 0;
        for(Integer value : map.values()) {
            size += value;
        }

        return size;
    }

    public void put(String term, int count) {
        map.put(term, count);
    }

    public Integer get(String term) {
        Integer count = map.get(term);

        return count == null ? 0 : count;
    }

    // key값 출력
    public Set<String> keySet() {
        return map.keySet();
    }

    public void incrementTermCount(String term) {
        put(term, get(term) + 1);
    }

    public void processElements(Elements paragraphs) {
        for(Node node: paragraphs) {
            processTree(node);
        }
    }

    public void processTree(Node root) {
        for(Node node : new WikiNodeIterable(root)) {
            if(node instanceof TextNode) {
                processText(((TextNode) node).text());
            }
        }
    }

    public void processText(String text) {
        String[] array = text.replaceAll("\\pP", " ").
                toLowerCase().
                split("\\s+");

        for(int i=0; i<array.length; i++) {
            String term = array[i];
            incrementTermCount(term);
        }
    }

    public void printCounts() {
        for(String key:keySet()) {
            Integer count = get(key);
            System.out.println(key + ", " + count);
        }
        System.out.println("Total of all counts = " + size());
    }

    public static void main(String[] args) throws IOException {
        String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        WikiFetcher wf = new WikiFetcher();
        Elements paragraphs = wf.fetchWikipedia(url);

        TermCounter counter = new TermCounter(url.toString());
        counter.processElements(paragraphs);
        counter.printCounts();
    }

}
