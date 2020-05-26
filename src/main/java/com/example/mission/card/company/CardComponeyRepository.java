package com.example.mission.card.company;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface CardComponeyRepository extends Repository<RequestMessage, Long> {

    void save(RequestMessage requestMessage);

    RequestMessage findById(long id);

    List<RequestMessage> findAll();
}
