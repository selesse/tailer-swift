package com.selesse.tailerswift;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            // GUI version?
        }
        // command line version
        else {
            TailerSwift tailerSwift = new TailerSwift(args);
            tailerSwift.run();
        }
    }
}
