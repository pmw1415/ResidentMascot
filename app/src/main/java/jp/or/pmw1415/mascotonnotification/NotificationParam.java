package jp.or.pmw1415.mascotonnotification;

import android.app.PendingIntent;

/**
 * Created by pmw1415 on 2015/11/21.
 */
public class NotificationParam {
	public String title;
	public String contentText;
	public int smallIcon;
	public int largeIcon;
	public boolean ongoing;
	public boolean autoCancel;
	public PendingIntent pendingIntent;

	/**
	 * コンストラクタ
	 *
	 * @param title_
	 * @param contentText_
	 * @param smallIcon_
	 * @param largeIcon_
	 * @param ongoing_
	 * @param autoCancel_
	 * @param pendingIntent_
	 */
	public NotificationParam(String title_, String contentText_,
							 int smallIcon_, int largeIcon_,
							 boolean ongoing_, boolean autoCancel_,
							 PendingIntent pendingIntent_) {
		title = title_;
		contentText = contentText_;
		smallIcon = smallIcon_;
		largeIcon = largeIcon_;
		ongoing = ongoing_;
		autoCancel = autoCancel_;
		pendingIntent = pendingIntent_;
	}
}
