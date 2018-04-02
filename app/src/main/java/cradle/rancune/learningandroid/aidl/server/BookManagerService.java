package cradle.rancune.learningandroid.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;

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

    private final CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
    private final RemoteCallbackList<IOnNewBookArrivedListener> mListeners = new RemoteCallbackList<>();
    private volatile boolean mStopSignal = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mBooks.add(new Book("1", "Effective Java"));
        mBooks.add(new Book("2", "Go In Action"));
        new Thread(new AddBookWorker()).start();
    }

    @Override
    public void onDestroy() {
        stopWorker();
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
            mBooks.add(book);
            onNewBookArrived(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBooks;
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.unregister(listener);
        }
    };

    private void onNewBookArrived(Book book) {
        int size = mListeners.beginBroadcast();
        for (int i = 0; i < size; ++i) {
            IOnNewBookArrivedListener listener = mListeners.getBroadcastItem(i);
            try {
                listener.onNewBookArrived(book);
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
                    mBooks.add(new Book(timeStamp, "Book " + timeStamp));
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
