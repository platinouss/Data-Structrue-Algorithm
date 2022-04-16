package com.datastructure;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class WikiNodeExample {
    public static void main(String[] args) throws IOException {
        String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        // url 문서 다운로드 및 파싱 후
        // DOM 트리를 나타내는 Document 객체를 반환
        // Document 객체는 트리를 탐색하고 노드 선택 메서드 제공
        Connection conn = Jsoup.connect(url);
        Document doc = conn.get();

        // 내용을 선택하고 단락 추출
        // getElementById 메서드는 인자로 받은 id 필드를 같는 요소를 트리에서 찾음
        Element content = doc.getElementById("mw-content-text");

        // select 메서드는 인자 값과 일치하는 태그를 가진 모든 요소를 반환
        Elements paras = content.select("p");
        Element firstPara = paras.get(0);

        recursiveDFS(firstPara);
        System.out.println();

        iterativeDFS(firstPara);
        System.out.println();

        Iterable<Node> iter = new WikiNodeIterable(firstPara);
        for (Node node : iter) {
            if (node instanceof TextNode) {
                System.out.print(node);
            }
        }
    }

    // 반복적 DFS
    private static void iterativeDFS(Node root) {
        Deque<Node> stack = new ArrayDeque<Node>();
        stack.push(root);

        while(!stack.isEmpty()) {
            Node node = stack.pop();
            if(node instanceof TextNode) {
                System.out.print(node);
            }

            List<Node> nodes = new ArrayList<Node>(node.childNodes());
            Collections.reverse(nodes);

            for(Node child: nodes) {
                stack.push(child);
            }
        }
    }

    // 재귀적 DFS
    private static void recursiveDFS(Node node) {
        if(node instanceof TextNode) {
            System.out.print(node);
        }

        for(Node child: node.childNodes()) {
            recursiveDFS(child);
        }
    }
}
