package com.example.dictionary.View.ListAdapter;

import java.util.Collection;

public interface BaseListAdapter<T> {
    void addItem(T item);
    void addItems(Collection<T> items);
    void clearItems();
}
