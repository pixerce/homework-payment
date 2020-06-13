package com.example.mission.payment.repository;

import com.example.mission.payment.repository.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT card FROM Card card WHERE card.hashKey =:hashKey group by card.hashKey")
    Card findByHashKey(@Param("hashKey") String hashKey);
}
