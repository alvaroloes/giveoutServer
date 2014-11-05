package com.capstone.potlatch.models;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    public User findByUsername(String username);
//    @Query(nativeQuery = true, value = "select u.* from User as u left join Gift as g on u.id = g.user_id group by u.id order by count(g.id) desc") //Todo: hacer la query con el count
//    public List<User> aQuery();
}
