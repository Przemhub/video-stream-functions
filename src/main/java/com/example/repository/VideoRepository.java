package com.example.repository;

import com.example.entity.Video;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;


import java.util.UUID;

@Repository
public interface VideoRepository extends CrudRepository<Video, UUID> {

    Iterable<Video> findAll();
}
