package com.capstone.potlatch.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRepository extends PagingAndSortingRepository<Gift, Long> {

    Page<Gift> findByGiftChainIsNotNull(Pageable pageable);
    @Query("select g from Gift g where g.giftChain is not null and ?1 not member of g.markedInappropriateByUserIds")
    Page<Gift> findByGiftChainIsNotNullAndUserNotFlagAsInappropriate(long userId, Pageable pageable);

	Page<Gift> findByGiftChainIsNotNullAndTitleLike(String title, Pageable pageable);
    @Query("select g from Gift g where g.giftChain is not null and g.title like ?1 and ?2 not member of g.markedInappropriateByUserIds")
	Page<Gift> findByGiftChainIsNotNullAndTitleLikeAndUserNotFlagAsInappropriate(String title, long userId, Pageable pageable);

	Page<Gift> findByUserId(long userId, Pageable pageable);
	Page<Gift> findByUserIdAndTitleLike(long userId, String title, Pageable pageable);
    long countByGiftChain(GiftChain giftChain);
}
