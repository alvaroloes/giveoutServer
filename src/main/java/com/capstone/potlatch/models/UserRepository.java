package com.capstone.potlatch.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    public User findByUsername(String username);
//    @Query(nativeQuery = true, value = "select u.* from User as u left join Gift as g on u.id = g.user_id group by u.id order by count(g.id) desc") //Todo: hacer la query con el count
//    public List<User> aQuery();
    @Query("select new User(u, count(g)) from User u left join u.gifts g group by u.id order by count(g) DESC")
    public Page<User> getUsersOrderedByNumberOfGifts(Pageable pageable);
}
