package ch.epfl.sweng.smartTabs.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import ch.epfl.sweng.smartTabs.R;

/**
 * Display the help window
 */
public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Enabling Up / Back navigation
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.title_help));

    }
}
