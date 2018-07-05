package cradle.rancune.learningandroid.aidl.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.util.List;

import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.aidl.IBookManager;
import cradle.rancune.learningandroid.aidl.IOnNewBookArrivedListener;
import cradle.rancune.learningandroid.aidl.model.Book;
import cradle.rancune.learningandroid.aidl.server.BookManagerService;
import cradle.rancune.learningandroid.BaseActivity;

/**
 * Created by Rancune@126.com 2018/4/1.
 */
public class BookManagerClientActivity extends BaseActivity implements View.OnClickListener {

    private IBookManager mBookManager;

    @Override
    public void initView() {
        setContentView(R.layout.activity_aidl_client);
        findViewById(R.id.add_book).setOnClickListener(this);
        findViewById(R.id.get_book_list).setOnClickListener(this);
    }

    @Override
    public void initData() {
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mBookManager != null) {
            try {
                mBookManager.unregisterListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_book: {
                if (mBookManager != null) {
                    try {
                        String timeStamp = String.valueOf(SystemClock.uptimeMillis());
                        Book book = new Book(timeStamp, "手动AddBook " + timeStamp);
                        mBookManager.add(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case R.id.get_book_list: {
                if (mBookManager != null) {
                    List<Book> list = null;
                    try {
                        list = mBookManager.getBookList();
                        Log.d(TAG, String.valueOf(list));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected:" + Thread.currentThread().getName());
            mBookManager = IBookManager.Stub.asInterface(service);
            try {
                mBookManager.registerListener(mListener);
                service.linkToDeath(mDeathRecipient, 0);
                // 因为这方法运行在主线程，一般这里也不能调用耗时的操作
                List<Book> list = mBookManager.getBookList();
                Log.d(TAG, list.getClass().getName());
                Log.d(TAG, String.valueOf(list));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:" + Thread.currentThread().getName());
        }
    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mBookManager == null) {
                return;
            }
            Log.d(TAG, "binderDied thread:" + Thread.currentThread().getName());
            mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBookManager = null;
            // 重新绑定
            Intent intent = new Intent(BookManagerClientActivity.this, BookManagerService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    };

    private final IOnNewBookArrivedListener mListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            Log.d(TAG, "onNewBookArrived:" + book.toString());
        }
    };


}
