package com.alex.abumov.myappportfolio;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {

    Context app_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Variables */
        app_context = getApplicationContext();

        // ----------------------------------------------------------------
        // ----------------------------------------------------------------

        /* Buttons */
        Button mna_button_spotify = (Button) findViewById(R.id.mna_button_spotify);
        Button mna_button_scores = (Button) findViewById(R.id.mna_button_scores);
        Button mna_button_library = (Button) findViewById(R.id.mna_button_library);
        Button mna_button_bigger = (Button) findViewById(R.id.mna_button_bigger);
        Button mna_button_bacon = (Button) findViewById(R.id.mna_button_bacon);
        Button mna_button_capstone = (Button) findViewById(R.id.mna_button_capstone);

        // ----------------------------------------------------------------
        // ----------------------------------------------------------------

        /* Listeners */
        View.OnClickListener testListener = new View.OnClickListener() {
            public void onClick(View v) {
                testToast();
            }
        };
        mna_button_spotify.setOnClickListener(testListener); // mna_button_spotify listener
        // ----------------------------------------------------------------
        mna_button_scores.setOnClickListener(testListener); // mna_button_scores listener
        // ----------------------------------------------------------------
        mna_button_library.setOnClickListener(testListener); // mna_button_library listener
        // ----------------------------------------------------------------
        mna_button_bigger.setOnClickListener(testListener); // mna_button_bigger listener
        // ----------------------------------------------------------------
        mna_button_bacon.setOnClickListener(testListener); // mna_button_bacon listener
        // ----------------------------------------------------------------
        mna_button_capstone.setOnClickListener(testListener); // mna_button_capstone listener
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void testToast() {
        CharSequence text = "This button will launch my capstone project!";
        Toast.makeText(app_context, text, Toast.LENGTH_SHORT).show();
    }
}
