package com.example.test_sel.Callback;

import java.util.ArrayList;

public interface CallBack_ArrayReady<T> {

    void arrayReady(ArrayList<T> array);
    void error();

}
