package com.capstone.potlatch.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.List;


@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class GiftChain {
    @Transient
    @JsonIgnore
    public boolean allowAccessToGifts = false;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private String name;

    @OneToMany(mappedBy="giftChain", fetch = FetchType.EAGER)
    private List<Gift> gifts;

    public GiftChain() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Gift> getGifts() {
        if (!allowAccessToGifts) {
            return null;
        }
        return gifts;
    }

    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
    }


}
