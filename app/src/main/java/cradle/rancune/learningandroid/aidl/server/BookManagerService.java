package cradle.rancune.learningandroid.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import cradle.rancune.learningandroid.aidl.IBookManager;
import cradle.rancune.learningandroid.aidl.IOnNewBookArrivedListener;
import cradle.rancune.learningandroid.aidl.model.Book;

/**
 * Created by Rancune@126.com 2018/3/30.
 */
public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";

    private final CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
    private final RemoteCallbackList<IOnNewBookArrivedListener> mListeners = new RemoteCallbackList<>();
    private volatile boolean mStopSignal = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BookManagerService onCreate");
        mBooks.add(new Book("1", "Effective Java"));
        mBooks.add(new Book("2", "Go In Action"));
        new Thread(new AddBookWorker()).start();
    }

    @Override
    public void onDestroy() {
        stopWorker();
        Log.d(TAG, "BookManagerService onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private final IBinder mBinder = new IBookManager.Stub() {
        @Override
        public void add(Book book) throws RemoteException {
            addBook(book);
            Log.d(TAG, "IBinder add:" + Thread.currentThread().getName());
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.d(TAG, "IBinder getBookList:" + Thread.currentThread().getName());
            return mBooks;
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
            Log.d(TAG, "IBinder registerListener:" + Thread.currentThread().getName());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.unregister(listener);
            Log.d(TAG, "IBinder unregisterListener:" + Thread.currentThread().getName());
        }
    };

    private void addBook(Book book) {
        if (book == null) {
            return;
        }
        mBooks.add(book);
        int size = mListeners.beginBroadcast();
        for (int i = 0; i < size; ++i) {
            IOnNewBookArrivedListener listener = mListeners.getBroadcastItem(i);
            try {
                listener.onNewBookArrived(book);
                Log.d(TAG, "onNewBookArrived:" + Thread.currentThread().getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mListeners.finishBroadcast();
    }

    private class AddBookWorker implements Runnable {

        @Override
        public void run() {
            while (!mStopSignal) {
                try {
                    String timeStamp = String.valueOf(SystemClock.uptimeMillis());
                    Book book = new Book(timeStamp, "Book " + timeStamp);
                    addBook(book);
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void stopWorker() {
        mStopSignal = true;
    }

}
