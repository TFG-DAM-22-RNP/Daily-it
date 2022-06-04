package rafael.ninoles.parra.dailyit.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Class to converts dates into parcelable dates
 */
public class MyDate extends Date implements Parcelable {
    public MyDate() {
        super();
    }

    public MyDate(long date) {
        super(date);
    }

    public MyDate(int year, int month, int date) {
        super(year, month, date);
    }

    public MyDate(int year, int month, int date, int hrs, int min) {
        super(year, month, date, hrs, min);
    }

    public MyDate(int year, int month, int date, int hrs, int min, int sec) {
        super(year, month, date, hrs, min, sec);
    }

    public MyDate(String s) {
        super(s);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public void readFromParcel(Parcel source) {
    }

    protected MyDate(Parcel in) {
    }

    public static final Parcelable.Creator<MyDate> CREATOR = new Parcelable.Creator<MyDate>() {
        @Override
        public MyDate createFromParcel(Parcel source) {
            return new MyDate(source);
        }

        @Override
        public MyDate[] newArray(int size) {
            return new MyDate[size];
        }
    };
}
