package jp.or.pmw1415.mascotonnotification;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by pmw1415 on 2015/11/21.
 */
public class NotificationController {
	private static final int NotificationBarId = R.string.notification_id;
	private Context mContext;

	public NotificationController(Context context) {
		mContext = context;
	}

	/**
	 * Notification表示/非表示
	 *
	 * @param enabled
	 * @param param
	 */
	public void setNotification(boolean enabled, NotificationParam param) {
		NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);

		if (enabled && param != null) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
			builder.setContentTitle(param.title);
			builder.setContentText(param.contentText);
			builder.setSmallIcon(param.smallIcon);
			builder.setOngoing(param.ongoing);
			builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), param.largeIcon));
			builder.setColor(param.color);
			if (param.pendingIntent != null) {
				builder.setContentIntent(param.pendingIntent);
			}
			builder.setAutoCancel(param.autoCancel);

			manager.notify(NotificationBarId, builder.build());
		}
		else {
			manager.cancel(NotificationBarId);
		}
	}

	/**
	 * Notification表示状態更新
	 *
	 * @param enabled
	 */
	public void updateNotification(boolean enabled) {
		if (enabled) {
			Intent intent = new Intent();
			intent.setClassName(mContext.getPackageName(), NotificationAnimationService.class.getName());
			mContext.startService(intent);
		}
		else {
			setNotification(false, null);
		}
	}
}
