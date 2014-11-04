package com.capstone.potlatch.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
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
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String title;
	private String description;
	private String imageUrl;
	@ElementCollection
	private Set<Long> touchedByUserIds = new HashSet<Long>();
	@ElementCollection
	private Set<Long> markedInappropriateByUserIds = new HashSet<Long>();

    @ManyToOne(optional=false)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    private GiftChain giftChain;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    @JsonIgnore // Ignore property when serializing
    public GiftChain getGiftChain() {
        return giftChain;
    }

    @JsonProperty // Include property when deserializing
    public void setGiftChain(GiftChain giftChain) {
        this.giftChain = giftChain;
    }

    public Long getGiftChainId() {
        if (giftChain != null) {
            return giftChain.getId();
        }
        return null;
    }

    public String getGiftChainName() {
        if (giftChain != null) {
            return giftChain.getName();
        }
        return null;
    }
}
