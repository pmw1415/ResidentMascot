package jp.or.pmw1415.residentmascot;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

/**
 * Created by pmw1415 on 2016/01/13.
 */
public class MyBatteryManager {
	private static final String TAG = MyBatteryManager.class.getSimpleName();

	private Context mContext;
	private String mKeyLowBatteryFlag;

	public MyBatteryManager(Context context) {
		mContext = context;
		mKeyLowBatteryFlag = mContext.getString(R.string.low_battery_flag_key);
	}

	/**
	 * バッテリ残量低下フラグ更新
	 *
	 * 現在のバッテリ残量とシステムリソースの比較により判定。
	 * 設定値を更新し、戻り値で更新後の値を返す。
	 *
	 * 充電中はバッテリ残量値に関係無くfalse固定
	 *
	 * @return 更新後の設定値
	 */
	public boolean updateLowBatteryFlag() {
		boolean isLowBattery;

		// バッテリ情報取得
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = mContext.registerReceiver(null, filter);
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

		// システムリソースから低残量判定しきい値を取得
		int resIdLowBatteryWarningLevel = mContext.getResources().getSystem().getIdentifier("config_lowBatteryWarningLevel", "integer", "android");
		int lowBatteryWarningLevel = mContext.getResources().getInteger(resIdLowBatteryWarningLevel);

		DebugLogger.output(TAG, "status=" + status + "level=" + level + ", warningLevel=" + lowBatteryWarningLevel);

		// 判定
		isLowBattery = (status != BatteryManager.BATTERY_STATUS_CHARGING) && (level <= lowBatteryWarningLevel);

		// 保存
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(mKeyLowBatteryFlag, isLowBattery);
		editor.commit();

		return isLowBattery;
	}
}
