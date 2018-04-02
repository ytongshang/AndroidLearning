// IBookManager.aidl
package cradle.rancune.learningandroid.aidl;

import cradle.rancune.learningandroid.aidl.model.Book;
import cradle.rancune.learningandroid.aidl.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    void add(in Book book);
    List<Book> getBookList();
    void registerListener (in IOnNewBookArrivedListener listener);
    void unregisterListener(in IOnNewBookArrivedListener listener);
}
