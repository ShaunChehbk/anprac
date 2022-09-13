package com.example.mousecontrol;

import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.List;
import java.util.UUID;

public class ConsoleEmu extends ArrayAdapter {
    Context context;
    int resource;
    private List<String> console;

    public ConsoleEmu(Context context, int resource, List<String> console) {
        super(context, resource, console);
        this.context = context;
        this.resource = resource;
        this.console = console;
    }
    @Override
    public int getCount() {
        return console.size();
    }
    @Override
    public String getItem(int position) {
        return console.get(position);
    }
    public void Log(String Tag, String log) {
        console.add(Tag + ": " + log);
        notifyDataSetChanged();
    }
    public void Log(String Tag, UUID uuid) {
        console.add(Tag + ": " + uuid.toString());
        notifyDataSetChanged();
    }
    public void Rest() {
        console.clear();
        notifyDataSetChanged();
    }
}
