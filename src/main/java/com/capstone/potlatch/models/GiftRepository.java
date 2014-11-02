package com.capstone.potlatch.models;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRepository extends CrudRepository<Gift, Long>{
	// Find all gifts with a matching title
	public Collection<Gift> findByTitle(String title);
}
