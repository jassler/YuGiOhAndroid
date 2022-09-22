package com.kaasbrot.boehlersyugiohapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface HistoryElement {
    View render(LayoutInflater inflater, ViewGroup parent);
}
