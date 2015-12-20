package jp.or.pmw1415.mascotonnotification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by pmw1415 on 2015/11/22.
 */
public class SettingFragment extends PreferenceFragment
		implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
	private Context mContext;
	// key
	private String mKeyNotificationEnabled;

	private boolean mBeforeSettingEnabled;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);

		mContext = this.getActivity();
		mKeyNotificationEnabled = this.getString((R.string.notification_enabled_key));

		CheckBoxPreference prefEnabled = (CheckBoxPreference)findPreference(mKeyNotificationEnabled);
		prefEnabled.setOnPreferenceChangeListener(this);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		mBeforeSettingEnabled = sharedPref.getBoolean(mKeyNotificationEnabled, false);
	}

	@Override
	public void onStop() {
		super.onStop();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean nowSettingEnabled = sharedPref.getBoolean(mKeyNotificationEnabled, false);

		showNotification(mContext, nowSettingEnabled);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}

	/**
	 * Notification表示/非表示
	 *
	 * @param context
	 * @param enabled
	 */
	private void showNotification(Context context, boolean enabled) {
		Intent intent = new Intent();
		intent.setClassName(mContext.getPackageName(),  SettingActivity.class.getName());
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationParam param = new NotificationParam(
				mContext,
				mContext.getString(R.string.app_name),
				"",
				R.mipmap.ic_launcher ,R.mipmap.ic_launcher,
				true, false, pendingIntent
		);

		NotificationController notificationController = new NotificationController(context);
		notificationController.setNotification(enabled, param);
	}
}
