package com.capstone.potlatch.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    public User findByUsername(String username);

    @Query("select new User(u, count(g), -1L) from User u left join u.gifts g group by u.id order by count(g) DESC")
    public Page<User> getUsersOrderedByNumberOfGiftsDesc(Pageable pageable);

    @Query("select new User(u, -1L, count(tbu)) from Gift g right join g.user u left join g.touchedByUserIds tbu group by u.id order by count(tbu) DESC")
    public Page<User> getUsersOrderedByNumberOfTouchesDesc(Pageable pageable);
}
