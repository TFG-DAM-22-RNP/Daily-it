package rafael.ninoles.parra.dailyit.model;

public class User {
    private String AuthUID;
    private String imgProfile;
    private String name;

    public String getAuthUID() {
        return AuthUID;
    }

    public void setAuthUID(String authUID) {
        AuthUID = authUID;
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
        AuthUID = authUID;
        this.imgProfile = imgProfile;
        this.name = name;
    }
}
