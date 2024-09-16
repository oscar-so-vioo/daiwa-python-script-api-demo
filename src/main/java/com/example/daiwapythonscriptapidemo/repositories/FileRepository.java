package com.example.daiwapythonscriptapidemo.repositories;
import com.example.daiwapythonscriptapidemo.entities.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<FileEntity, String> {

}
