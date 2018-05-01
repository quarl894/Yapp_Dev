package yapp.dev_diary.Lock.core;

import android.app.Application;
import android.util.Log;

import yapp.dev_diary.Setting.LockActivity;

public class LockManager {

	private volatile static LockManager instance;
	private AppLock curAppLocker;

	public static LockManager getInstance() {
		synchronized (LockManager.class) {
			if (instance == null) {
				instance = new LockManager();
			}
		}
		return instance;
	}

	public void enableAppLock(Application app) {
		if (curAppLocker == null) {
			curAppLocker = new AppLockImpl(app);
		}
		curAppLocker.enable();
	}

	public boolean isAppLockEnabled() {
		curAppLocker.addIgnoredActivity(LockActivity.class);
		if (curAppLocker == null) {
			Log.e("a","a");
			return false;
		} else {
			Log.e("b","b");
			return true;
		}
	}
	public boolean isnotAppLockEnabled() {
		curAppLocker.removeIgnoredActivity(LockActivity.class);
		if (curAppLocker == null) {
			Log.e("a","a");
			return false;
		} else {
			Log.e("b","b");
			return true;
		}
	}

	public void setAppLock(AppLock appLocker) {
		if (curAppLocker != null) {
			curAppLocker.disable();
		}
		curAppLocker = appLocker;
	}

	public AppLock getAppLock() {
		return curAppLocker;
	}
}
