package jp.or.pmw1415.mascotonnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by pmw1415 on 2015/12/27.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("MyBroadcastReceiver", "action = " + action);

		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			// アニメーション表示タイミング調整
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}
		}

		Intent intentService = new Intent();
		intentService.setClassName(context.getPackageName(), NotificationAnimationService.class.getName());
		context.startService(intentService);
	}

	/**
	 * レシーバ登録
	 *
	 * manifestで登録できないアクションを登録する。
	 *
	 * @param context
	 */
	public void register(Context context) {
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
	public void unregister(Context context) {
		try {
			context.getApplicationContext().unregisterReceiver(this);
		} catch (IllegalArgumentException e) {
		}
	}
}
