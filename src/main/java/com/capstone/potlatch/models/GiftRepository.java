package com.capstone.potlatch.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRepository extends PagingAndSortingRepository<Gift, Long> {
	Page<Gift> findByTitleLike(String title, Pageable pageable);
	Page<Gift> findByUserId(long userId, Pageable pageable);
	Page<Gift> findByUserIdAndTitleLike(long userId, String title, Pageable pageable);
    long countByGiftChain(GiftChain giftChain);
}
