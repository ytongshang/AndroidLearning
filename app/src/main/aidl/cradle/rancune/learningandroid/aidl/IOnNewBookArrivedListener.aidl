// IOnNewBookArrivedListener.aidl
package cradle.rancune.learningandroid.aidl;

import cradle.rancune.learningandroid.aidl.model.Book;

// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {

    void onNewBookArrived(in Book book);
}
