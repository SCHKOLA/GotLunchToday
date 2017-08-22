/*
 * MIT License
 *
 * Copyright 2017 Niklas Merkelt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.schkola.kitchenscanner.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONException;

import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.task.StatTask;
import de.schkola.kitchenscanner.util.FragmentMgr;
import de.schkola.kitchenscanner.util.LunchListAdapter;

public class StatsActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.collecting_data));
        dialog.setMessage(getString(R.string.collecting_data_lunch));
        setContent(R.layout.content_stats_overview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list:
                setContent(R.layout.content_stats_list);
                item.setVisible(false);
                menu.getItem(0).setVisible(true);
                return true;
            case R.id.action_chart:
                setContent(R.layout.content_stats_overview);
                item.setVisible(false);
                menu.getItem(1).setVisible(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setContent(int layout) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, FragmentMgr.newInstance(layout)).commit();
        switch (layout) {
            case R.layout.content_stats_overview:
                new StatTask(dialog, (jsonObject) -> {
                    try {
                        ((TextView) findViewById(R.id.orderedA)).setText(jsonObject.getString("lunchA"));
                        ((TextView) findViewById(R.id.gotA)).setText(jsonObject.getString("gotLunchA"));
                        ((TextView) findViewById(R.id.getA)).setText(String.valueOf(jsonObject.getInt("lunchA") - jsonObject.getInt("gotLunchA")));
                        ((TextView) findViewById(R.id.orderedB)).setText(jsonObject.getString("lunchB"));
                        ((TextView) findViewById(R.id.gotB)).setText(jsonObject.getString("gotLunchB"));
                        ((TextView) findViewById(R.id.getB)).setText(String.valueOf(jsonObject.getInt("lunchB") - jsonObject.getInt("gotLunchB")));
                        ((TextView) findViewById(R.id.orderedS)).setText(jsonObject.getString("lunchS"));
                        ((TextView) findViewById(R.id.gotS)).setText(jsonObject.getString("gotLunchS"));
                        ((TextView) findViewById(R.id.getS)).setText(String.valueOf(jsonObject.getInt("lunchS") - jsonObject.getInt("gotLunchS")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).execute();
                break;
            case R.layout.content_stats_list:
                new StatTask(dialog, (jsonObject) -> {
                    try {
                        ExpandableListView listView = findViewById(R.id.listview);
                        listView.setAdapter(new LunchListAdapter(this, jsonObject.getJSONArray("getLunchA"), jsonObject.getJSONArray("getLunchB"), jsonObject.getJSONArray("getLunchS")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).execute();
                break;
        }
    }
}
