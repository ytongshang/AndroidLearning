package cradle.rancune.learningandroid.aidl.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rancune@126.com 2018/3/29.
 */
public class Book implements Parcelable {
    private String mId;
    private String mName;

    public Book(String id, String name) {
        this.mId = id;
        this.mName = name;
    }

    protected Book(Parcel in) {
        mId = in.readString();
        mName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
    }

    public void readFromParcel(Parcel in) {
        mId = in.readString();
        mName = in.readString();
    }

    @Override
    public String toString() {
        return "Book{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                '}';
    }
}
