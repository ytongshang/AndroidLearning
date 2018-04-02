package cradle.rancune.learningandroid.aidl.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.aidl.IBookManager;
import cradle.rancune.learningandroid.aidl.model.Book;
import cradle.rancune.learningandroid.aidl.server.BookManagerService;
import cradle.rancune.learningandroid.base.BaseActivity;

/**
 * Created by Rancune@126.com 2018/4/1.
 */
public class BookManagerClientActivity extends BaseActivity{

    private IBookManager mBookManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBookManager != null) {
            try {
                List<Book> list = mBookManager.getBookList();
                Log.d(TAG, list.getClass().getName());
                Log.d(TAG, String.valueOf(list));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBookManager = IBookManager.Stub.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mBookManager == null) {
                return;
            }
            mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBookManager = null;
        }
    };
}
