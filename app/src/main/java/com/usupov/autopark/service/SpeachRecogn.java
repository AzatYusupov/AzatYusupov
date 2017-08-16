package com.usupov.autopark.service;

import android.content.Context;

import com.usupov.autopark.config.ApiURIConstants;
import com.usupov.autopark.http.HttpHandler;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Azat on 04.03.2017.
 */

public class SpeachRecogn {
    static class Sort implements Comparable<Sort> {
        String key, value;

        @Override
        public int compareTo(Sort o) {
            if (this.key.length() < o.key.length())
                return 1;
            if (this.key.length() > o.key.length())
                return -1;
            return this.key.compareTo(o.key);
        }
        public Sort(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
    public static String changeWordsToDigits(String str) {
        ArrayList<Sort> map = new ArrayList<>();

        map.add(new Sort("раз", "1"));
        map.add(new Sort("один", "1"));

        map.add(new Sort("два", "2"));

        map.add(new Sort("три", "3"));

        map.add(new Sort("четыре", "4"));

        map.add(new Sort("пять", "5"));

        map.add(new Sort("шесть", "6"));

        map.add(new Sort("семь", "7"));

        map.add(new Sort("восемь", "8"));

        map.add(new Sort("девять", "9"));

        map.add(new Sort("эй", "a"));
        map.add(new Sort("а", "a"));

        map.add(new Sort("би", "b"));
        map.add(new Sort("б", "b"));
        map.add(new Sort("beat", "b"));
        map.add(new Sort("bi", "b"));

        map.add(new Sort("cи", "c"));
        map.add(new Sort("c", "c"));

        map.add(new Sort("ди", "d"));
        map.add(new Sort("д", "d"));

        map.add(new Sort("и", "e"));
        map.add(new Sort("е", "e"));

        map.add(new Sort("эф", "f"));
        map.add(new Sort("ф", "f"));

        map.add(new Sort("джи", "g"));
        map.add(new Sort("г", "g"));

        map.add(new Sort("эйч", "h"));
        map.add(new Sort("хаш", "h"));


        map.add(new Sort("джей", "j"));
        map.add(new Sort("jay", "j"));
        map.add(new Sort("чай", "j"));
        map.add(new Sort("джейк", "j"));

        map.add(new Sort("окей", "k"));
        map.add(new Sort("кей", "k"));
        map.add(new Sort("к", "k"));

        map.add(new Sort("эл", "l"));
        map.add(new Sort("л", "l"));

        map.add(new Sort("эм", "m"));
        map.add(new Sort("м", "m"));

        map.add(new Sort("эн", "n"));
        map.add(new Sort("н", "n"));

        map.add(new Sort("ооо", "0"));
        map.add(new Sort("о", "0"));

        map.add(new Sort("пи", "p"));
        map.add(new Sort("п", "p"));

        map.add(new Sort("ку", "q"));
        map.add(new Sort("чем", "q"));

        map.add(new Sort("эр", "r"));
        map.add(new Sort("р", "r"));

        map.add(new Sort("эс", "s"));
        map.add(new Sort("ц", "c"));

        map.add(new Sort("так", "t"));
        map.add(new Sort("то", "t"));
        map.add(new Sort("т", "t"));

        map.add(new Sort("у", "u"));

        map.add(new Sort("вий", "v"));
        map.add(new Sort("вей", "v"));
        map.add(new Sort("ви", "v"));
        map.add(new Sort("в", "v"));
        map.add(new Sort("way", "v"));

        map.add(new Sort("давлю", "w"));
        map.add(new Sort("даблю", "w"));
        map.add(new Sort("дабл ю", "w"));

        map.add(new Sort("икс", "x"));
        map.add(new Sort("экс", "x"));
        map.add(new Sort("х", "x"));

        map.add(new Sort("игорек", "y"));
        map.add(new Sort("игры", "y"));
        map.add(new Sort("вай", "y"));
        map.add(new Sort("лай", "y"));


        map.add(new Sort("зет", "z"));
        map.add(new Sort("зед", "z"));
        map.add(new Sort("з", "z"));

        map.add(new Sort(" ", ""));
        map.add(new Sort("-", ""));
        map.add(new Sort("\\(", ""));
        map.add(new Sort("\\)", ""));
        map.add(new Sort("\\.", ""));
        map.add(new Sort(",", ""));


        map.add(new Sort("вида блин", "vw"));
        map.add(new Sort("вида был", "vw"));
        map.add(new Sort("baby", "bb"));
        map.add(new Sort("вида", "vw"));

        Collections.sort(map);
        for (Sort s : map) {
            str = str.replaceAll(s.key, s.value.trim());
        }
        return str;
    }
    public static String vinSpeach(ArrayList<String> text, Context context) {

        if (text==null)
            return "";

        String res = "";

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        for (int i = 0; i < text.size(); i++) {
            String variant = text.get(i).trim();
            String url = ApiURIConstants.API + "log/vin";
            HttpHandler handler = new HttpHandler();
            HashMap<String, String> data = new HashMap<>();
            if (i != text.size()-1)
                data.put("vin", variant);
            else
                data.put("vin", variant+"\n");
            int result = handler.postWithOneFile(url, data, null, context.getApplicationContext(), false).getStatusCode();
            if (result== HttpStatus.SC_OK) {
                System.out.println("variant="+variant+" has been sent to the server");
            }
            else
                System.out.println("variant="+variant+" hasn't been sent to the server");
            variant = variant.toLowerCase();
            System.out.println(variant+"   ------");
            variant = changeWordsToDigits(variant);
            System.out.println(variant+"    ++++++++");
            if (res.length()==0)
                res = variant;
        }
        return res;
    }
    public static String partToNormal(String reconNatedtext) {
        reconNatedtext = changeWordsToDigits(reconNatedtext);
        String afterFiltrText = "";
        for (int i = 0; i < reconNatedtext.length(); i++) {
            char w = reconNatedtext.charAt(i);
            if (Character.isLetter(w) || Character.isDigit(w) || w=='-' || w==' ') {
                afterFiltrText += w;
            }
        }
        return afterFiltrText;
    }
}
