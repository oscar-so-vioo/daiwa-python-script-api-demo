package com.example.daiwapythonscriptapidemo.repositories;
import com.example.daiwapythonscriptapidemo.entities.ReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<ReportEntity, String> {

    ReportEntity findByTemplateName(String templateName);
}
