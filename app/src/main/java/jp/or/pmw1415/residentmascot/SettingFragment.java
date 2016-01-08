package jp.or.pmw1415.residentmascot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
		mKeyNotificationEnabled = this.getString(R.string.notification_enabled_key);
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

		String keyVersionName = this.getString(R.string.version_name_key);
		Preference prefVersion = (Preference)findPreference(keyVersionName);
		prefVersion.setSummary(getVersionName(mContext));

	}

	@Override
	public void onStop() {
		super.onStop();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean nowSettingEnabled = sharedPref.getBoolean(mKeyNotificationEnabled, false);

		// 常駐設定更新
		updateResident(mContext, nowSettingEnabled);

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
	 * アプリのバージョン名取得
	 *
	 * @param context
	 * @return バージョン名
	 */
	private String getVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		String versionName = "";
		try {
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionName;
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
	 * 常駐設定更新
	 *
	 * @param context
	 * @param enabled
	 */
	private void updateResident(Context context, boolean enabled) {
		Intent intent = new Intent();
		intent.setClassName(context.getPackageName(), ResidentService.class.getName());
		if (enabled) {
			context.startService(intent);
		}
		else {
			context.stopService(intent);
		}
	}
}
