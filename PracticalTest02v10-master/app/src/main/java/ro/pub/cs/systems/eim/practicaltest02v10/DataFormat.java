package ro.pub.cs.systems.eim.practicaltest02v10;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DataFormat {
    private final String res;

    public DataFormat(String res) {
        this.res = res;
    }

    public String getRes() {
        return res;
    }

    @Override
    public String toString() {
        return "Result of operation is: " + res;
    }
}
