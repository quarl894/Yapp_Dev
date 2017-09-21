package yapp.dev_diary.Lock;

import android.app.Application;

import yapp.dev_diary.Lock.core.LockManager;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LockManager.getInstance().enableAppLock(this);
	}

}
