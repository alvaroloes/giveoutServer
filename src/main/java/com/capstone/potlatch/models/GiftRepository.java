package com.capstone.potlatch.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRepository extends PagingAndSortingRepository<Gift, Long> {
	public Page<Gift> findByTitleLike(String title, Pageable pageable);
}
