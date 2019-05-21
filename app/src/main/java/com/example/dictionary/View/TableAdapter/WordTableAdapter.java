package com.example.dictionary.View.TableAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dictionary.Model.RoomDB.Entity.Word;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class WordTableAdapter extends TableDataAdapter<Word> {
    public WordTableAdapter(Context context, List<Word> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Word word = getRowData(rowIndex);
        View renderedView = null;

        switch(columnIndex) {
            case 0:
                renderedView = renderText(word.Word_String);
                break;
            case 1:
                renderedView = renderText("1");
                break;
            case 2:
                SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                if (word.getLastestLog() != null)
                    renderedView = renderText(format.format(word.getLastestLog().Log_Date));
                break;
            case 3:
                if (word.getLastestLog() != null)
                    renderedView = renderText(String.valueOf(word.Logs.size()));
                break;
        }
            return renderedView;
    }

    private TextView renderText(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        return  textView;
    }

}
