package rafael.ninoles.parra.dailyit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

@IgnoreExtraProperties
public class Task implements Parcelable {
    private String id;
    private String title;
    private String description;
    @ServerTimestamp
    private Date expires;
    @ServerTimestamp
    private Date created;
    private String status;
    private String userUid;
    private Category category;
    private String categoryId;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Exclude
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Exclude
    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public Task(){
        //Required for firebase
    }

    public Task(String id, String title, String description, Date expires, Date created, String status, String userUid) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expires = expires;
        this.created = created;
        this.status = status;
        this.userUid = userUid;
    }

    public Task(Task task){
        this.id = task.id;
        this.title = task.title;
        this.description = task.description;
        this.expires = task.expires;
        this.created = task.created;
        this.status = task.status;
        this.userUid = task.userUid;
        this.category = task.category;
        this.categoryId = task.categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeLong(this.expires != null ? this.expires.getTime() : -1);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeString(this.status);
        dest.writeString(this.userUid);
        dest.writeParcelable(this.category, flags);
        dest.writeString(this.categoryId);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.description = source.readString();
        long tmpExpires = source.readLong();
        this.expires = tmpExpires == -1 ? null : new Date(tmpExpires);
        long tmpCreated = source.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.status = source.readString();
        this.userUid = source.readString();
        this.category = source.readParcelable(Category.class.getClassLoader());
        this.categoryId = source.readString();
    }

    protected Task(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        long tmpExpires = in.readLong();
        this.expires = tmpExpires == -1 ? null : new Date(tmpExpires);
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.status = in.readString();
        this.userUid = in.readString();
        this.category = in.readParcelable(Category.class.getClassLoader());
        this.categoryId = in.readString();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public boolean isTheSame(Task task) {
        System.out.println("ORIGINAL");
        System.out.println(this.printInfo());
        System.out.println("COMPARANDO");
        System.out.println(task.printInfo());
        return title.equals(task.title) && description.equals(task.description)
                && expires.getTime() == task.expires.getTime()
                && created.getTime() == task.created.getTime()
                && status.equals(task.status)
                && categoryId.equals(task.categoryId);
    }

    public String printInfo() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", expires=" + expires.getTime() +
                ", created=" + created.getTime() +
                ", status='" + status + '\'' +
                ", userUid='" + userUid + '\'' +
                ", categoryId='" + categoryId + '\'' +
                '}';
    }
}
