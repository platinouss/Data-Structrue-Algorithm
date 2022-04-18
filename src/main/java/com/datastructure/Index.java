package com.datastructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.jsoup.select.Elements;

public class Index {
    private Map<String, Set<TermCounter>> index = new HashMap<>();

    public void add(String term, TermCounter tc) {
        Set<TermCounter> set = get(term);

        if(set == null) {
            set = new HashSet<TermCounter>();
            index.put(term, set);
        }

        set.add(tc);
    }

    public Set<TermCounter> get(String term) {
        return index.get(term);
    }

    public void printIndex() {
        for(String term: keySet()) {
            System.out.println(term);

            Set<TermCounter> tcs = get(term);
            for(TermCounter tc: tcs) {
                Integer count = tc.get(term);
                System.out.println("   " + tc.getLabel() + " " + count);
            }
        }
    }

    public Set<String> keySet() {
        return index.keySet();
    }

    public void indexPage(String url, Elements paragraphs) {
        TermCounter counter = new TermCounter(url);
        counter.processElements(paragraphs);

        for(String s : counter.keySet()) {
            add(s, counter);
        }

    }

    public static void main(String[] args) throws IOException {
        WikiFetcher wf = new WikiFetcher();
        Index indexer = new Index();

        String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        Elements paragraphs = wf.fetchWikipedia(url);
        indexer.indexPage(url, paragraphs);

        url = "https://en.wikipedia.org/wiki/Programming_language";
        paragraphs = wf.fetchWikipedia(url);
        indexer.indexPage(url, paragraphs);

        indexer.printIndex();
    }
}
