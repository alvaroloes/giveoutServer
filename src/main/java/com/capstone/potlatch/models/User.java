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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private String username;
    private transient String password;
    private boolean enabled = true; //This should be default to true

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
}
