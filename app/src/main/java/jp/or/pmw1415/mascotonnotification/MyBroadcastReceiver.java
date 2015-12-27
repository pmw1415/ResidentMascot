package jp.or.pmw1415.mascotonnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pmw1415 on 2015/12/27.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent intentService = new Intent();
			intentService.setClassName(context.getPackageName(), NotificationAnimationService.class.getName());
			context.startService(intentService);
		}

	}
}
