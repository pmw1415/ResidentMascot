package jp.or.pmw1415.residentmascot;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * Created by pmw1415 on 2015/12/12.
 */
public class NotificationAnimationService extends Service implements Runnable {
	private static final String TAG = NotificationAnimationService.class.getSimpleName();
	// アニメーションの点滅回数
	private static final int AnimationBlinkNum = 2;
	// アニメーションの点滅間隔
	private static final int AnimationBlinkInterval = 300;

	private String mKeyNotificationEnabled;
	private String mKeyLowBatteryFlag;

	private Context mContext;
	private ConditionVariable mCondition;

	@Override
	public void onCreate() {
		super.onCreate();

		mKeyNotificationEnabled = this.getString(R.string.notification_enabled_key);
		mKeyLowBatteryFlag = this.getString(R.string.low_battery_flag_key);

		mContext = this;
		mCondition = new ConditionVariable(false);

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
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean isLowBattery = sharedPref.getBoolean(mKeyLowBatteryFlag, getLowBatteryFlag());

		Intent intent = new Intent();
		intent.setClassName(mContext.getPackageName(),  SettingActivity.class.getName());
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationParam param = new NotificationParam(
				mContext.getString(R.string.notification_title_prnm),
				mContext.getString(R.string.notification_message_prnm),
				R.mipmap.icon_prnm1, R.mipmap.icon_prnm1_large,
				Color.rgb(0x7c, 0x00, 0x14),
				true, false, pendingIntent
		);

		if (isLowBattery) {
			param.smallIcon = R.mipmap.icon_prnm4;
			param.largeIcon = R.mipmap.icon_prnm4_large;
			param.contentText = mContext.getString(R.string.notification_message_low_battery_prnm);
			showNotification(mContext, param);
		}
		else {
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
		}

		// サービス終了
		NotificationAnimationService.this.stopSelf();
	}

	/**
	 * バッテリ残量低下フラグ算出
	 *
	 * バッテリ残量とシステムリソースから判定。
	 * 充電中はバッテリ残量に関係なくfalse固定
	 *
	 * @return
	 */
	public boolean getLowBatteryFlag() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = mContext.registerReceiver(null, filter);
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

		int resIdLowBatteryWarningLevel = mContext.getResources().getSystem().getIdentifier("config_lowBatteryWarningLevel", "integer", "android");
		int lowBatteryWarningLevel = mContext.getResources().getInteger(resIdLowBatteryWarningLevel);

		DebugLogger.output(TAG, "status=" + status + "level=" + level + ", warningLevel=" + lowBatteryWarningLevel);

		return (status != BatteryManager.BATTERY_STATUS_CHARGING) && (level <= lowBatteryWarningLevel);
	}
}
