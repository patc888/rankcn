package com.xeo.rankcn;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static Map<Character, Item> counts = new HashMap<>();
    static Set<Character> known = new HashSet<>();
    static boolean verbose = false;
    static boolean common = false;
    static List<URL> urls = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-v":
                    verbose = true;
                    break;
                case "-common":
                    common = true;
                    break;
                default:
                    urls.add(new URL(args[i]));
                    break;
            }
        }

        // Load in known characters
        BufferedReader rd = new BufferedReader(new FileReader("bin/known"));
        String inputLine;
        while ((inputLine = rd.readLine()) != null) {
            for (char ch : inputLine.toCharArray()) {
                known.add(ch);
            }
        }
        rd.close();

        FileWriter txtWr = new FileWriter("chars.txt");
        txtWr.write("// Unknown Chars " + new Date() + "\n");
        if (common) {
            // Read from common chars file
            rd = new BufferedReader(new FileReader("3000chars.txt"));
            while ((inputLine = rd.readLine()) != null) {
                if (verbose) {
                    System.out.println(inputLine);
                }
                for (char ch : inputLine.toCharArray()) {
                    if (ch > 0xff && !known.contains(ch)) {
                        txtWr.write(ch);
                        txtWr.write('\n');
                    }
                }
            }
            rd.close();
        } else {
            // Read all URLs
            FileWriter sentWr = new FileWriter("phrases.txt");
            for (URL url : urls) {
                rd = new BufferedReader(new InputStreamReader(url.openStream()));
                read(rd, sentWr);
                rd.close();
            }
            sentWr.close();

            // Dump the list of characters
            List<Item> items = new ArrayList<>(counts.values());
            Collections.sort(items);
            for (int i = 0; i < items.size(); i++) {
                txtWr.write(items.get(i).ch + "\n");
            }
            rd.close();
        }
        txtWr.close();
    }

    static void read(BufferedReader rd, Writer sentWr) throws Exception {
        boolean inSentence = false;
        String inputLine;
        while ((inputLine = rd.readLine()) != null) {
            if (verbose) {
                System.out.println(inputLine);
            }
            for (char ch : inputLine.toCharArray()) {
                if (ch > 0xff) {
                    if (!known.contains(ch)
                            ) {
                        Item item = counts.get(ch);
                        if (item == null) {
                            item = new Item();
                            item.ch = ch;
                            item.count = 0;
                            counts.put(ch, item);
                        }
                        item.count++;
                    }
                    sentWr.write(ch);
                    inSentence = true;
                } else if (inSentence) {
                    sentWr.write("\n");
                    inSentence = false;
                }
            }
        }
    }

    static class Item implements Comparable<Item> {
        Character ch;
        int count;

        public int compareTo(Item item) {
            return item.count - count;
        }
    }
}
