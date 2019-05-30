package com.example.dictionary.View.TableAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.LogType;
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
                SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                if (word.getLastestLog() != null)
                    renderedView = renderText(format.format(word.getLastestLog().Log_Date));
                break;
            case 2:
                if (word.getLastestLog() != null) {
                    int size = 0;
                    for(Log log  : word.Logs) {
                        if (log.LogType == LogType.WordLocalDetail || log.LogType == LogType.WordWebDetail)
                            size++;
                    }
                    renderedView = renderText(String.valueOf(size));
                }
                break;
            case 3:
                if (word.Logs != null) {
                    int size = 0;
                    for(Log log  : word.Logs) {
                        if (log.LogType == LogType.WordMemorized)
                            size++;
                    }
                    renderedView = renderText(String.valueOf(size));
                }
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
