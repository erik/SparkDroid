package com.boredomist.SparkDroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SparkDroidActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(Intent.ACTION_VIEW, null,
				getApplicationContext(), SearchActivity.class);
		startActivity(intent);

	}
}

/*
 * public class SparkDroidActivity extends TabActivity { private TabHost
 * mTabHost;
 * 
 * @Override public void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState); setContentView(R.layout.main);
 * 
 * mTabHost = (TabHost) findViewById(android.R.id.tabhost); mTabHost.setup();
 * 
 * mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
 * 
 * setupTab(SearchActivity.class, "Tab 1"); setupTab(SearchActivity.class,
 * "Tab 2"); setupTab(SearchActivity.class, "Search");
 * 
 * }
 * 
 * private void setupTab(final Class<?> c, final String tag) { View tabview =
 * createTabView(mTabHost.getContext(), tag);
 * 
 * TabSpec tspec = mTabHost.newTabSpec(tag).setIndicator(tabview);
 * tspec.setContent(new Intent(this, c)); mTabHost.addTab(tspec);
 * 
 * mTabHost.setCurrentTab(0); }
 * 
 * private static View createTabView(final Context context, final String text) {
 * View view = LayoutInflater.from(context) .inflate(R.layout.tabs_bg, null);
 * TextView tv = (TextView) view.findViewById(R.id.tabsText); tv.setText(text);
 * return view; } }
 */