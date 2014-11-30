package com.capstone.potlatch.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple object to represent a video and its URL for viewing.
 * 
 * You probably need to, at a minimum, add some annotations to this
 * class.
 * 
 * You are free to add annotations, members, and methods to this
 * class. However, you probably should not change the existing
 * methods or member variables. If you do change them, you need
 * to make sure that they are serialized into JSON in a way that
 * matches what is expected by the auto-grader.
 * 
 * @author mitchell
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIdentityInfo(generator =  ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Gift {
    public static final String SIZE_FULL = "full";
    public static final String SIZE_MEDIUM = "medium";
    public static final String SIZE_SMALL = "small";

    @Transient
    @JsonIgnore
    public boolean allowAccessToGiftChain = false;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String title;
	private String description;
	private String imageUrlFull;
	private String imageUrlMedium;
	private String imageUrlSmall;
	@ElementCollection
	private Set<Long> touchedByUserIds = new HashSet<Long>();
	@ElementCollection
	private Set<Long> markedInappropriateByUserIds = new HashSet<Long>();

    @ManyToOne(optional=false, fetch = FetchType.EAGER)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    private GiftChain giftChain;

    private Date createdAt;
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

	public Gift() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getImageUrlSmall() {
        return imageUrlSmall;
    }

    public void setImageUrlSmall(String imageUrlSmall) {
        this.imageUrlSmall = imageUrlSmall;
    }

    public Set<Long> getTouchedByUserIds() {
        return touchedByUserIds;
    }

    public void setTouchedByUserIds(Set<Long> touchedByUserIds) {
        this.touchedByUserIds = touchedByUserIds;
    }

    public Set<Long> getMarkedInappropriateByUserIds() {
        return markedInappropriateByUserIds;
    }

    public void setMarkedInappropriateByUserIds(Set<Long> markedInappropriateByUserIds) {
        this.markedInappropriateByUserIds = markedInappropriateByUserIds;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GiftChain getGiftChain() {
        if (!allowAccessToGiftChain) {
            return null;
        }
        return giftChain;
    }

    public void setGiftChain(GiftChain giftChain) {
        this.giftChain = giftChain;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
