package com.unimib.petsphere.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class User implements Parcelable {
    private String userName;
    private String email;
    private String uid;
    private String idToken;

    public User() {
        // costrutture vuoto
    }

    public User(String userName, String email, String uid) {
        this.userName = userName;
        this.email = email;
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public String getIdToken() {
        return idToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", idToken='" + uid + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.email);
        dest.writeString(this.uid);
    }

    public void readFromParcel(Parcel source) {
        this.userName = source.readString();
        this.email = source.readString();
        this.uid = source.readString();
    }

    protected User(Parcel in) {
        this.userName = in.readString();
        this.email = in.readString();
        this.uid = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
