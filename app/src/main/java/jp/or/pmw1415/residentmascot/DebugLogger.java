package jp.or.pmw1415.residentmascot;

import android.util.Log;

/**
 * Created by pmw1415 on 2015/12/31.
 */
public class DebugLogger {

	private DebugLogger() {}

	/**
	 * デバッグログ出力(debugビルド時のみ)
	 *
	 * @param tag
	 * @param message
	 */
	public static void output(String tag, String message) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, message);
		}
	}
}
