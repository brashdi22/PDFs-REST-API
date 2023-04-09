package com.backend.pdfs.services;

import com.backend.pdfs.entities.Sentences;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface SentencesRepository extends MongoRepository<Sentences, String> {
    @Query(value = "{'sentences' : { $regex: ?0, $options: 'i' } }",
            fields = "{ 'sentences': { $filter: { input: '$sentences', as: 'sentence', cond: { $regexMatch: { input: '$$sentence', regex: ?0, options: 'i' } } } }, '_id': 1 }")
    List<Sentences> findBySentencesContaining(String keyword);


    @Query(value = "{'_id': ?0, 'sentences' : { $regex: ?1, $options: 'i' } }",
            fields = "{ 'sentences': { $filter: { input: '$sentences', as: 'sentence', cond: { $regexMatch: { input: '$$sentence', regex: ?1, options: 'i' } } } }, '_id': 1 }")
    Sentences countOccurrences(String id, String keyword);

}