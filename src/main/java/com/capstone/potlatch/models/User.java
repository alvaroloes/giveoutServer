package com.capstone.potlatch.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "users", //In order to match the Oauth user table
       indexes = @Index(columnList = "username", unique = true))
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    public static final String SIZE_FULL = "full";
    public static final String SIZE_MEDIUM = "medium";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private String username;
    private transient String password;
    private boolean enabled = true; //This should be default to true
    private String imageUrlFull;
    private String imageUrlMedium;

    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Gift> gifts;

    @Transient
    private Long giftCount;
    @Transient
    private Long giftTouches;

    public User() {}

    // This constructor is used in a Jpa query
    public User(User u, long giftCount, long giftTouches) {
        this.id = u.id;
        this.username = u.username;
        this.enabled = u.enabled;
        this.imageUrlFull = u.imageUrlFull;
        this.imageUrlMedium = u.imageUrlMedium;
        this.gifts = u.gifts;
        if (giftCount >= 0) {
            this.giftCount = giftCount;
        }
        if (giftTouches >= 0) {
            this.giftTouches = giftTouches;
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Gift> getGifts() {
        return gifts;
    }

    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Long getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(Long giftCount) {
        this.giftCount = giftCount;
    }

    public Long getGiftTouches() {
        return giftTouches;
    }

    public void setGiftTouches(Long giftTouches) {
        this.giftTouches = giftTouches;
    }

    public String getImageUrlFull() {
        return imageUrlFull;
    }

    public void setImageUrlFull(String imageUrlFull) {
        this.imageUrlFull = imageUrlFull;
    }

    public String getImageUrlMedium() {
        return imageUrlMedium;
    }

    public void setImageUrlMedium(String imageUrlMedium) {
        this.imageUrlMedium = imageUrlMedium;
    }
}
