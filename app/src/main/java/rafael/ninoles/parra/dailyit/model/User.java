package rafael.ninoles.parra.dailyit.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;


/**
 * Model class to users using Firestore
 */
@IgnoreExtraProperties
public class User {
    private String id;
    private String imgProfile;
    private String name;
    private List<Category> categories;
    private String email;

    public void addCategory(Category category){
        this.categories.add(category);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String authUID) {
        id = authUID;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {
    }

    public User(String imgProfile, String name) {
        this.imgProfile = imgProfile;
        this.name = name;
    }

    public User(String authUID, String imgProfile, String name) {
        this.id = authUID;
        this.imgProfile = imgProfile;
        this.name = name;
    }
}
