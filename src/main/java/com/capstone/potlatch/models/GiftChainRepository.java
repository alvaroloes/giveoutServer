package com.capstone.potlatch.models;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftChainRepository extends CrudRepository<GiftChain, Long>{
}
