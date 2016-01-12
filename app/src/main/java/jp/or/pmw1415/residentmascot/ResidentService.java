package jp.or.pmw1415.residentmascot;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by pmw1415 on 2015/12/30.
 */
public class ResidentService extends Service {
	private Context mContext;
	private static MyBroadcastReceiver mReceiver;

	@Override
	public void onCreate() {
		DebugLogger.output(getClass().getSimpleName(), "onCreate(): register receiver");
		mContext = this;
		mReceiver = new MyBroadcastReceiver();

		// レシーバ登録
		mReceiver.updateReceiver(mContext, true);

		// startForeground()でNotification出さない対応
		startForeground(0, new Notification());
	}

	@Override
	public void onDestroy() {
		DebugLogger.output(getClass().getSimpleName(), "onDestroy(): unregister receiver");
		// レシーバ登録解除
		mReceiver.updateReceiver(mContext, false);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
