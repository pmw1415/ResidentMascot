package jp.or.pmw1415.mascotonnotification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
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
	private String mKeyIconType;

	private boolean mBeforeSettingEnabled;

	private String[] mListPrefIconType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);

		mContext = this.getActivity();
		mKeyNotificationEnabled = this.getString((R.string.notification_enabled_key));
		mKeyIconType = this.getString(R.string.icon_type_key);

		CheckBoxPreference prefEnabled = (CheckBoxPreference)findPreference(mKeyNotificationEnabled);
		prefEnabled.setOnPreferenceChangeListener(this);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		mBeforeSettingEnabled = sharedPref.getBoolean(mKeyNotificationEnabled, false);

		mListPrefIconType = this.getResources().getStringArray(R.array.icon_type);
		ListPreference prefIconType = (ListPreference)findPreference(this.getString(R.string.icon_type_key));
		prefIconType.setOnPreferenceChangeListener(this);

		String iconType = sharedPref.getString(mKeyIconType, mListPrefIconType[0]);
		if (prefIconType.getValue() == null) {
			prefIconType.setValue(iconType);
		}
		prefIconType.setSummary(iconType);

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
		if (preference.getKey().equals(mKeyIconType)) {
			ListPreference prefIconType = (ListPreference)preference;
			prefIconType.setSummary((String)newValue);
		}

		return true;
	}

	/**
	 * Notification表示/非表示
	 *
	 * @param context
	 * @param enabled
	 */
	private void showNotification(Context context, boolean enabled) {
		if (enabled) {
			Intent intent = new Intent();
			intent.setClassName(context.getPackageName(), NotificationAnimationService.class.getName());
			context.startService(intent);
		}
		else {
			NotificationController notificationController = new NotificationController(context);
			notificationController.setNotification(false, null);

		}
	}
}
