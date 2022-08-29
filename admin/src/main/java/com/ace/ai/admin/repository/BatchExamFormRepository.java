package com.ace.ai.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.ai.admin.datamodel.BatchExamForm;

@Repository
public interface BatchExamFormRepository extends JpaRepository<BatchExamForm,Integer>{
    public List<BatchExamForm> findByBatch_IdAndExamForm_DeleteStatus(Integer batchId, Boolean status);
}
