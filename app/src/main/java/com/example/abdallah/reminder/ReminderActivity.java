package com.example.abdallah.reminder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReminderActivity extends AppCompatActivity {


    private ListView LV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        LV = (ListView) findViewById(R.id._list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.reminder_row,
                R.id.row_text,
                new String[]{"first record", "second record", "third record"});
        LV.setAdapter(arrayAdapter);



    }
}
