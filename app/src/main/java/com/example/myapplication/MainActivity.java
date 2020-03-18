package com.example.myapplication;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private RemindersDbAdapter mDbAdapter;
    private RemindersSimpleCursorAdapter mCursorAdapter;

    private int getIdFromPosition(int nC) {
        return (int)mCursorAdapter.getItemId(nC);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView =  findViewById(R.id.list_view);
        listView.setDivider(null);

        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();

        Cursor cursor = mDbAdapter.fetchAllReminders();
//from columns defined in the db
        String[] from = new String[]{
                RemindersDbAdapter.COL_CONTENT
        };
//to the ids of views in the layout
        int[] to = new int[]{
                R.id.row_text
        };
        mCursorAdapter = new RemindersSimpleCursorAdapter(

                MainActivity.this,

                R.layout.reminders_row,

                cursor,

                from,

                to,

                0);

        listView.setAdapter(mCursorAdapter);

        if (savedInstanceState == null) {
            mDbAdapter.deleteAllReminders();
//Add some data
            mDbAdapter.createReminder(".", true);
            mDbAdapter.createReminder("..", false);
            mDbAdapter.createReminder("...", false);
            mDbAdapter.createReminder("....", false);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                ListView modeListView = new ListView(MainActivity.this);
                String[] modes = new String[] { "Edit Reminder", "Delete Reminder" };
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//edit reminder
                        if (position == 0) {
                            int nId = getIdFromPosition(masterListPosition);
                            Reminder reminder = mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(reminder);
//delete reminder
                        } else {
                            mDbAdapter.deleteReminderById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void fireCustomDialog(final Reminder reminder) {
// custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation = (reminder != null);
//this is for an edit
        if (isEditOperation) {
            titleView.setText("Edit Reminder");
            checkBox.setChecked(reminder.getImportant() == 1);
            editCustom.setText(reminder.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminderText = editCustom.getText().toString();
                if (isEditOperation) {
                    Reminder reminderEdited = new Reminder(reminder.getId(),
                            reminderText, checkBox.isChecked() ? 1 : 0);
                    mDbAdapter.updateReminder(reminderEdited);
//this is for new reminder
                } else {
                    mDbAdapter.createReminder(reminderText, checkBox.isChecked());
                }
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });
        Button buttonCancel = (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_reminder:
//create new Reminder
                fireCustomDialog(null);
                return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return false;
        }
    }
}
