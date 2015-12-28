package jp.or.pmw1415.mascotonnotification;

import android.content.Context;
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

	private String[] mListPrefIconType;
	private String[] mListTextIconType;

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

		mListPrefIconType = this.getResources().getStringArray(R.array.icon_type_value);
		mListTextIconType = this.getResources().getStringArray(R.array.icon_type);
		ListPreference prefIconType = (ListPreference)findPreference(this.getString(R.string.icon_type_key));
		prefIconType.setOnPreferenceChangeListener(this);

		String iconTypeValue = sharedPref.getString(mKeyIconType, mListPrefIconType[0]);
		if (prefIconType.getValue() == null) {
			// 初期値を設定状態にする
			prefIconType.setValue(iconTypeValue);
		}
		prefIconType.setSummary(mListTextIconType[Integer.valueOf(iconTypeValue)]);

	}

	@Override
	public void onStop() {
		super.onStop();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean nowSettingEnabled = sharedPref.getBoolean(mKeyNotificationEnabled, false);

		// レシーバ登録状態更新
		updateReceiver(mContext, nowSettingEnabled);

		// Notification表示/非表示
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
			prefIconType.setSummary(mListTextIconType[Integer.valueOf((String)newValue)]);
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
		NotificationController notificationController = new NotificationController(context);
		notificationController.updateNotification(enabled);
	}

	/**
	 * レシーバ登録状態更新
	 *
	 * @param context
	 * @param enabled
	 */
	private void updateReceiver(Context context, boolean enabled) {
		MyBroadcastReceiver receiver = new MyBroadcastReceiver();
		receiver.updateReceiver(context, enabled);
	}
}
