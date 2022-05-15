package com.datastructure;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class JedisIndex {
    // redis db의 각 객체는 string형인 유일한 키로 식별
    private Jedis jedis;
    public JedisIndex(Jedis jedis) { this.jedis = jedis; }

    // 주어진 검색어가 있는 URL을 포함한 레디스의 set
    // 주어진 검색어에 대한 redis key를 return
    // ex) 'the'단어를 포함한 URL을 얻으려면 'URLSet:the'라는 키로 set에 접근
    private String urlSetKey(String term) { return "URLSet:" + term; }
    // 각 검색어가 페이지에 등장한 횟수를 맵핑하는 레디스의 hash
    // 각 termcounter 객체의 키는 'TermCounter:'로 시작하고 조회하려는 url로 정의됨
    private String termCounterKey(String url) { return "TermCounter:" + url; }

    // 주어진 url에 대한 TermCounter가 있는지 확인
    public boolean isIndexed(String url) {
        String redisKey = termCounterKey(url);

        return jedis.exists(redisKey);
    }

    // 검색어(term)와 연결된 집합에 url 추가
    // label : url 문자열 저장된 파라미터
    public void add(String term, TermCounter tc) {
        jedis.sadd(urlSetKey(term), tc.getLabel());
    }

    // 검색어(term)을 찾아 url 집합 반환
    public Set<String> getURLs(String terms) {
        Set<String> s = jedis.smembers(urlSetKey(terms));

        return s;
    }

    // 검색어(term)가 포함된 url들을 추출한 다음,
    // 각 url별 검색어(term) 등장횟수를 저장한 map을 반환
    public Map<String, Integer> getCounts(String term) {
        Map<String, Integer> map = new HashMap<>();
        Set<String> urls = getURLs(term);

        for(String url: urls) {
            Integer count = getCount(url, term);
            map.put(url, count);
        }

        return map;
    }

    // 주어진 url과 검색어(term)에 대한 등장횟수 반환
    public Integer getCount(String url, String term) {
        String rediskey = termCounterKey(url);
        String count = jedis.hget(rediskey, term);

        return new Integer(count);
    }

    public void indexPage(String url, Elements paragraphs) {
        System.out.println("Indexing " + url);

        TermCounter tc = new TermCounter(url);
        tc.processElements(paragraphs);

        // redis에 TermCounter 추가
        pushTermCounterToRedis(tc);
    }

    public List<Object> pushTermCounterToRedis(TermCounter tc) {
        // 작은 동작을 다수에 수행하면 응답시간이 느려지므로,
        // Transaction 객체를 활용하여 다수의 연산을 그룹지어 명령 실행
        Transaction t = jedis.multi();

        String url = tc.getLabel();
        String hashname = termCounterKey(url);

        // 이미 index 되어있을 경우 old hash 삭제
        t.del(hashname);

        for(String term: tc.keySet()) {
            Integer count = tc.get(term);
            t.hset(hashname, term, count.toString());
            t.sadd(urlSetKey(term), url);
        }
        // exec 메서드를 호출하기 전까지 배치동작으로 쌓아둠
        List<Object> res = t.exec();

        return res;
    }

    // 인덱스 전체 출력 (테스트용)
    public void printIndex() {
        for(String term: termSet()) {
            System.out.println(term);

            Set<String> urls = getURLs(term);
            for(String url: urls) {
                Integer count = getCount(url, term);
                System.out.println(" " + url + " " + count);
            }
        }
    }

    public Set<String> termSet() {
        Set<String> keys = urlSetKeys();
        Set<String> terms = new HashSet<>();
        for(String key: keys) {
            String[] array = key.split(":");
            if(array.length < 2) {
                terms.add("");
            } else {
                terms.add(array[1]);
            }
        }

        return terms;
    }

    public Set<String> urlSetKeys() { return jedis.keys("URLSet:*"); }
    public Set<String> termCounterKeys() { return jedis.keys("TermCounter:*"); }
    public void deleteURLSets() {
        Set<String> keys = urlSetKeys();
        Transaction t = jedis.multi();
        for(String key: keys) {
            t.del(key);
        }
        t.exec();
    }

    public void deleteTermCounters() {
        Set<String> keys = termCounterKeys();
        Transaction t = jedis.multi();
        for(String key: keys) {
            t.del(key);
        }
        t.exec();
    }

    public void deleteAllKeys() {
        Set<String> keys = jedis.keys("*");
        Transaction t = jedis.multi();
        for(String key: keys) {
            t.del(key);
        }
        t.exec();
    }

    public static void main(String[] args) throws IOException {
        Jedis jedis = JedisMaker.make();
        JedisIndex index = new JedisIndex(jedis);

        // index.deleteTermCounters();
        // index.deleteURLSets();
        // index.deleteAllKeys();
        loadIndex(index);

        Map<String, Integer> map = index.getCounts("the");
        for(Entry<String, Integer> entry: map.entrySet()) {
            System.out.println(entry);
        }
    }

    private static void loadIndex(JedisIndex index) throws IOException {
        WikiFetcher wf = new WikiFetcher();

        String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        Elements paragraphs = wf.readWikipedia(url);
        index.indexPage(url, paragraphs);

        url = "https://en.wikipedia.org/wiki/Programming_language";
        paragraphs = wf.readWikipedia(url);
        index.indexPage(url, paragraphs);
    }
}
