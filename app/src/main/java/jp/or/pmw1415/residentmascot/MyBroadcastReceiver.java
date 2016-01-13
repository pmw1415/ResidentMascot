package jp.or.pmw1415.residentmascot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by pmw1415 on 2015/12/27.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = MyBroadcastReceiver.class.getSimpleName();
	private boolean isScreenOn;

	public MyBroadcastReceiver() {
		isScreenOn = true;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		DebugLogger.output(TAG, "action = " + action);

		String keyNotificationEnabled = context.getString(R.string.notification_enabled_key);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled = sharedPref.getBoolean(keyNotificationEnabled, false);

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			// システム起動完了時に有効設定時、常駐ON
			// 電源OFF中にバッテリ残量が変わる可能性があるため、バッテリ残量低下フラグを更新
			MyBatteryManager myBatteryManager = new MyBatteryManager(context);
			myBatteryManager.updateLowBatteryFlag();

			Intent intentService = new Intent();
			intentService.setClassName(context.getPackageName(), ResidentService.class.getName());
			context.startService(intentService);
		}
		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			isScreenOn = true;
			// アニメーション表示タイミング調整
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
			}
		}
		if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			isScreenOn = false;
		}

		if (action.equals(Intent.ACTION_BATTERY_LOW) ||
				action.equals(Intent.ACTION_BATTERY_OKAY) ||
				action.equals(Intent.ACTION_POWER_CONNECTED)) {
			String keyLowBatteryFlag = context.getString(R.string.low_battery_flag_key);
			boolean value = action.equals(Intent.ACTION_BATTERY_LOW);

			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean(keyLowBatteryFlag, value);
			editor.commit();
		}

		if (isScreenOn) {
			DebugLogger.output(TAG, "update notification");
			NotificationController notificationController = new NotificationController(context);
			notificationController.updateNotification(enabled);
		}
	}

	/**
	 * レシーバ登録
	 *
	 * manifestで登録できないアクションを登録する。
	 *
	 * @param context
	 */
	private void register(Context context) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		context.getApplicationContext().registerReceiver(this, filter);
	}

	/**
	 * レシーバ登録解除
	 *
	 * @param context
	 */
	private void unregister(Context context) {
		try {
			context.getApplicationContext().unregisterReceiver(this);
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * レシーバ登録状態更新
	 *
	 * 登録済みの状態で登録すると古いものが残り重複してしまうため、
	 * 登録解除は毎回行う(未登録時はIllegalArgumentExceptionが発生)
	 *
	 * @param context
	 * @param enabled
	 */
	public void updateReceiver(Context context, boolean enabled) {
		unregister(context);
		if (enabled) {
			register(context);
		}
	}
}
