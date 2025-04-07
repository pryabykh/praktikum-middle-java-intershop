package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE lower(i.title) like lower(concat('%', :name, '%')) ")
    Page<Item> findAllByNameLike(String name, Pageable pageable);
}
