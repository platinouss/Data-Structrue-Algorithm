package com.datastructure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import com.datastructure.Profiler.Timeable;

public class ProfileListAdd {
    public static void main(String[] args) {
        profileArrayListAddEnd();
        profileArrayListAddBeginning();
        profileLinkedListAddBeginning();
        profileLinkedListAddEnd();
    }

    public static void profileArrayListAddEnd() {
        Timeable timeable = new Timeable() {
            List<String> list;

            public void setup(int n) { list = new ArrayList<String>(); }

            public void timeMe(int n) {
                for(int i=0; i<n; i++) {
                    list.add("a string");
                }
            }
        };

        int startN = 4000;
        int endMillis = 1000;
        runProfiler("ArrayList add end", timeable, startN, endMillis);
    }

    public static void profileArrayListAddBeginning() {
        Timeable timeable = new Timeable() {
            List<String> list;

            public void setup(int n) { list = new ArrayList<String>(); }

            public void timeMe(int n) {
                for(int i=0; i<n; i++) {
                    list.add(0, "a string");
                }
            }
        };

        int startN = 4000;
        int endMillis = 10000;
        runProfiler("ArrayList add begin", timeable, startN, endMillis);
    }

    public static void profileLinkedListAddBeginning() {
        Timeable timeable = new Timeable() {
            List<String> list;

            public void setup(int n) {
                list = new LinkedList<String>();
            }

            public void timeMe(int n) {
                for (int i = 0; i < n; i++) {
                    list.add(0, "a string");
                }
            }
        };

        int startN = 128000;
        int endMillis = 2000;
        runProfiler("LinkedList add begin", timeable, startN, endMillis);
    }

    public static void profileLinkedListAddEnd() {
        Timeable timeable = new Timeable() {
            List<String> list;

            public void setup(int n) {
                list = new LinkedList<String>();
            }

            public void timeMe(int n) {
                for (int i = 0; i < n; i++) {
                    list.add("a string");
                }
            }
        };

        int startN = 64000;
        int endMillis = 1000;
        runProfiler("LinkedList add begin", timeable, startN, endMillis);
    }

    /**
     * @param timeable
     * @param startN
     * @param endMillies
     */
    private static void runProfiler(String title, Timeable timeable, int startN, int endMillies) {
        Profiler profiler = new Profiler(title, timeable);
        XYSeries series = profiler.timingLoop(startN, endMillies);

        profiler.plotResult(series);
    }

}
