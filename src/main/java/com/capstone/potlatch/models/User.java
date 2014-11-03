package com.capstone.potlatch.models;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "users") //In order to match the Oauth user table
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    //@Index(columnList = "username", unique = true) TODO: ver cómo hacer esto
    private String username;
    private String password;

    private boolean enabled; //This shoul be default to true

    @OneToMany(mappedBy="user")
    private List<Gift> gifts;

    public User() {}

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
}
