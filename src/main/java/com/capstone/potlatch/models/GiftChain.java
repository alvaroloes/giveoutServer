package com.capstone.potlatch.models;

import javax.persistence.*;
import java.util.List;


@Entity
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftChain {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private String name;

    @OneToMany(mappedBy="giftChain")
    @Transient
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
        return gifts;
    }

    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
    }


}
