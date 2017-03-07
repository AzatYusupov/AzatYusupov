package com.usupov.autopark.service;

import java.util.ArrayList;

/**
 * Created by Azat on 04.03.2017.
 */

public class SpeachRecogn {
    public static String vinSpeach(ArrayList<String> text) {
        for (int i = 0; i < text.size(); i++) {
            String variant = text.get(i);
            variant = variant.toLowerCase();
            System.out.println(variant+"   ------");
            variant = variant.replaceAll("раз", "1");
            variant = variant.replaceAll("один", "1");

            variant = variant.replaceAll("два", "2");

            variant = variant.replaceAll("три", "3");

            variant = variant.replaceAll("четыре", "4");

            variant = variant.replaceAll("пять", "5");

            variant = variant.replaceAll("шесть", "6");

            variant = variant.replaceAll("семь", "7");

            variant = variant.replaceAll("восемь", "8");

            variant = variant.replaceAll("девять", "9");


            variant = variant.replaceAll("эй", "a");
            variant = variant.replaceAll("а", "a");

            variant = variant.replaceAll("би", "b");
            variant = variant.replaceAll("б", "b");

            variant = variant.replaceAll("cи", "c");
            variant = variant.replaceAll("c", "c");

            variant = variant.replaceAll("ди", "d");
            variant = variant.replaceAll("д", "d");

            variant = variant.replaceAll("и", "e");
            variant = variant.replaceAll("е", "e");

            variant = variant.replaceAll("эф", "f");
            variant = variant.replaceAll("ф", "f");

            variant = variant.replaceAll("джи", "g");
            variant = variant.replaceAll("г", "g");

            variant = variant.replaceAll("эйч", "h");
            variant = variant.replaceAll("хаш", "h");


            variant = variant.replaceAll("джей", "j");
            variant = variant.replaceAll("jay", "j");
            variant = variant.replaceAll("чай", "j");
            variant = variant.replaceAll("джейк", "j");

            variant = variant.replaceAll("окей", "k");
            variant = variant.replaceAll("кей", "k");
            variant = variant.replaceAll("к", "k");

            variant = variant.replaceAll("эл", "l");
            variant = variant.replaceAll("л", "l");

            variant = variant.replaceAll("эм", "m");
            variant = variant.replaceAll("м", "m");

            variant = variant.replaceAll("эн", "n");
            variant = variant.replaceAll("н", "n");

            variant = variant.replaceAll("ооо", "0");
            variant = variant.replaceAll("о", "0");

            variant = variant.replaceAll("пи", "p");
            variant = variant.replaceAll("п", "p");

            variant = variant.replaceAll("ку", "q");
            variant = variant.replaceAll("чем", "q");

            variant = variant.replaceAll("эр", "r");
            variant = variant.replaceAll("р", "r");

            variant = variant.replaceAll("эс", "s");

            variant = variant.replaceAll("так", "t");
            variant = variant.replaceAll("то", "t");
            variant = variant.replaceAll("т", "t");

            variant = variant.replaceAll("у", "u");

            variant = variant.replaceAll("вий", "v");
            variant = variant.replaceAll("ви", "v");
            variant = variant.replaceAll("в", "v");

            variant = variant.replaceAll("давлю", "w");
            variant = variant.replaceAll("даблю", "w");
            variant = variant.replaceAll("дабл ю", "w");

            variant = variant.replaceAll("икс", "x");
            variant = variant.replaceAll("экс", "x");
            variant = variant.replaceAll("х", "x");

            variant = variant.replaceAll("игорек", "y");
            variant = variant.replaceAll("игры", "y");
            variant = variant.replaceAll("вай", "y");
            variant = variant.replaceAll("лай", "y");


            variant = variant.replaceAll("зет", "z");
            variant = variant.replaceAll("зед", "z");
            variant = variant.replaceAll("з", "z");

            variant = variant.replaceAll(" ", "");
            variant = variant.replaceAll("-", "");


            //78954621358741001
            System.out.println(variant+"    ++++++++");
            if (variant.length()==17)
                return variant;
//            return variant;
        }
        return "";
    }
}
