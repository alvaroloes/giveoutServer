package com.capstone.potlatch.models;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftChainRepository extends PagingAndSortingRepository<GiftChain, Long> {
}
