package jp.or.pmw1415.mascotonnotification;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * Created by pmw1415 on 2015/12/12.
 */
public class NotificationAnimationService extends Service implements Runnable {
	// アニメーションの点滅回数
	private static final int AnimationBlinkNum = 2;
	// アニメーションの点滅間隔
	private static final int AnimationBlinkInterval = 300;

	private String mKeyNotificationEnabled;

	private Context mContext;
	private ConditionVariable mCondition;

	@Override
	public void onCreate() {
		super.onCreate();

		mKeyNotificationEnabled = this.getString((R.string.notification_enabled_key));

		mContext = this;
		mCondition = new ConditionVariable(false);

		setReceiver(this);

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void onDestroy() {
		mCondition.open();
	}

	/**
	 * Notification表示
	 *
	 * @param context
	 * @param param
	 */
	private void showNotification(Context context, NotificationParam param) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled = sharedPref.getBoolean(mKeyNotificationEnabled, false);

		NotificationController notificationController = new NotificationController(context);
		notificationController.setNotification(enabled, param);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {
		Intent intent = new Intent();
		intent.setClassName(mContext.getPackageName(),  SettingActivity.class.getName());
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationParam param = new NotificationParam(
				mContext.getString(R.string.notification_title_prnm),
				mContext.getString(R.string.notification_message_prnm),
				R.mipmap.icon_prnm1, R.mipmap.icon_prnm1_large,
				true, false, pendingIntent
		);

		for (int i = 0; i < AnimationBlinkNum; i++) {
			param.smallIcon = R.mipmap.icon_prnm2;
			param.largeIcon = R.mipmap.icon_prnm2_large;
			showNotification(mContext, param);
			if (mCondition.block(AnimationBlinkInterval)) {
				break;
			}
			param.smallIcon = R.mipmap.icon_prnm3;
			param.largeIcon = R.mipmap.icon_prnm3_large;
			showNotification(mContext, param);
			if (mCondition.block(AnimationBlinkInterval)) {
				break;
			}
		}
		param.smallIcon = R.mipmap.icon_prnm1;
		param.largeIcon = R.mipmap.icon_prnm1_large;
		showNotification(mContext, param);

		// サービス終了
		NotificationAnimationService.this.stopSelf();
	}

	/**
	 * レシーバ登録
	 *
	 * manifestで登録できないアクションがあるため、有効化/無効化するタイミングで
	 * レシーバの登録/登録解除を行う。
	 *
	 * @param context
	 */
	private void setReceiver(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled = sharedPref.getBoolean(mKeyNotificationEnabled, false);

		MyBroadcastReceiver receiver = new MyBroadcastReceiver();
		if (enabled) {
			receiver.register(context);
		}
		else {
			receiver.unregister(context);
		}
	}
}
