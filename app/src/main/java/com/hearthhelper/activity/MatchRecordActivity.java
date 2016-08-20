package com.hearthhelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hearthhelper.MatchRecordViewController;
import com.hearthhelper.R;
import com.hearthhelper.popover.PopoverService;

/**
 * Activity for the main game recorder match_record.
 */
public class MatchRecordActivity extends Activity {

    @Override
    protected void onCreate(Bundle saveInstanceBundle) {
        super.onCreate(saveInstanceBundle);
        setContentView(R.layout.main);

        ViewGroup layout = (ViewGroup) findViewById(R.id.main_layout);

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.match_record, null);
        layout.addView(view);

        new MatchRecordViewController(view);
        startPopoverService();
    }

    private void startPopoverService() {
        Intent popoverServiceIntent = new Intent(this, PopoverService.class);
        startService(popoverServiceIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


}
