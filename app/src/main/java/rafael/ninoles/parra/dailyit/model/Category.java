package rafael.ninoles.parra.dailyit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Model class for Tasks categories using Firestore
 */
@IgnoreExtraProperties
public class Category implements Parcelable {
    private String id;
    private String color;
    private String name;
    private boolean deleteable = true;

    public void setDeleteable(boolean newStatus) {
        deleteable = newStatus;
    }

    public boolean isDeleteable() {
        return deleteable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category() {
    }

    public Category(String id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.color);
        dest.writeString(this.name);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.color = source.readString();
        this.name = source.readString();
    }

    protected Category(Parcel in) {
        this.id = in.readString();
        this.color = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
