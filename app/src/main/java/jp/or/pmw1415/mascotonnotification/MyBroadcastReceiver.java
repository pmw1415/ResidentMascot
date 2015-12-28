package jp.or.pmw1415.mascotonnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by pmw1415 on 2015/12/27.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("MyBroadcastReceiver", "action = " + action);

		String keyNotificationEnabled = context.getString((R.string.notification_enabled_key));
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled = sharedPref.getBoolean(keyNotificationEnabled, false);

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			// システム起動完了時に有効設定時、レシーバを登録
			updateReceiver(context, enabled);
		}
		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			// アニメーション表示タイミング調整
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}
		}

		NotificationController notificationController = new NotificationController(context);
		notificationController.updateNotification(enabled);
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
