package com.example.swifttarrow.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.swifttarrow.nytimessearch.R;
import com.example.swifttarrow.nytimessearch.fragments.DatePickerFragment;

import java.util.Calendar;

public class SearchFilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        AdapterView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        ((EditText) findViewById(R.id.etBeginDate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_order_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spSortOrder)).setAdapter(adapter);
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment dpFragment = new DatePickerFragment();
        dpFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, date);
        ((EditText) findViewById(R.id.etBeginDate)).setText(DateFormat.format("MM/dd/yyyy", c.getTime()));
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onSubmit(View v) {
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("beginDate", ((EditText) findViewById(R.id.etBeginDate)).getText().toString());
        data.putExtra("sortOrder", ((Spinner) findViewById(R.id.spSortOrder)).getSelectedItem().toString()); // ints work too
        data.putExtra("isArts", ((CheckBox) findViewById(R.id.cbArts)).isChecked());
        data.putExtra("isFashionAndStyle", ((CheckBox) findViewById(R.id.cbFashionStyle)).isChecked());
        data.putExtra("isSports", ((CheckBox) findViewById(R.id.cbSports)).isChecked());
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
