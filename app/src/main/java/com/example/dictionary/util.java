package com.example.dictionary;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.dictionary.Model.RoomDB.Entity.PronType;
import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Model.RoomDB.Entity.WordType;

import java.util.Comparator;

public class util {
    public static void sysout(String str) {
        System.out.println(str);
    }


    public static String stringMerger(String...param) {
        String result = "";
        for(int i = 0; i < param.length; i++) {
            result += param[i] + " ";
        }
        return result;
    }

    public static String wordTypeToString(WordType type) {
        if (type == null)
            return "";
        String result = null;
        switch(type) {
            case Noun:
                result = "명사";
                break;
            case Adjective:
                result = "형용사";
                break;
            case Adverb:
                result = "부사";
                break;
            case Conjunction:
                result = "접속사";
                break;
            case Determiner:
                result = "한정사";
                break;
            case Exclamation:
                result = "감탄사";
                break;
            case Preposition:
                result = "전치사";
                break;
            case Pronoun:
                result = "대명사";
                break;
            case Verb:
                result = "동사";
                break;
            default:
                result = "단어";
                break;
        }
        return result;
    }

    public static WordType StringToWordType(String type) {
        WordType wordType = null;
        switch(type.toLowerCase()) {
            case "noun":
                wordType = WordType.Noun;
                break;
            case "adjective":
                wordType = WordType.Adjective;
                break;
            case "adverb":
                wordType = WordType.Adverb;
                break;
            case "conjunction":
                wordType = WordType.Conjunction;
                break;
            case "determiner":
                wordType = WordType.Determiner;
                break;
            case "exclamation":
                wordType = WordType.Exclamation;
                break;
            case "preposition":
                wordType = WordType.Preposition;
                break;
            case "pronoun":
                wordType = WordType.Pronoun;
                break;
            case "verb":
                wordType = WordType.Verb;
                break;
            default:
                wordType = WordType.Unknown;
                break;
        }
        return wordType;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String pronTypeToString(PronType pronType) {
        if (pronType == PronType.UK) {
            return "영국";
        } else {
            return "미국";
        }
    }

    public static class WordDescender implements Comparator<Word> {

        @Override
        public int compare(Word o1, Word o2) {
            Log vl1 = o1.getLastestLog();
            Log vl2 = o2.getLastestLog();
            if (vl1 == null && vl2 != null)
                return 1;
            else if (vl2 == null && vl1 != null)
                return -1;
            else if (vl1 == null && vl2 == null)
                return 0;
            else
                return vl2.Log_Date.compareTo(vl1.Log_Date);
        }
    }

    public static class LogDescender implements Comparator<Log> {
        @Override
        public int compare(Log o1, Log o2) {
            return o2.Log_Date.compareTo(o1.Log_Date);
        }
    }
}
