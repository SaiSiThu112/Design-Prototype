package com.ace.ai.admin.service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ace.ai.admin.datamodel.BatchExamForm;
import com.ace.ai.admin.datamodel.ExamForm;
import com.ace.ai.admin.dtomodel.ExamScheduleDTO;
import com.ace.ai.admin.repository.BatchExamFormRepository;
import com.ace.ai.admin.repository.ExamFormRepository;

@Service
public class BatchExamFormService {
    
    @Autowired
    BatchExamFormRepository batchExamFormRepository;

    @Autowired
    ExamFormRepository examFormRepository;

    public List<ExamScheduleDTO> showExamScheduleTable(Integer batchId) throws ParseException{
        List<ExamScheduleDTO>examScheduleDTOList = new ArrayList<>();
        List<BatchExamForm>batchExamFormList = batchExamFormRepository.findByDeleteStatusAndBatch_IdAndExamForm_DeleteStatus(false, batchId, false);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        //current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedString = now.format(formatter);
        LocalDateTime formattedNow = LocalDateTime.parse(formattedString, dtf);
        
        for(BatchExamForm batchExamForm:batchExamFormList){
            ExamScheduleDTO examScheduleDTO = new ExamScheduleDTO();
            examScheduleDTO.setId(batchExamForm.getId());
            examScheduleDTO.setExamName(batchExamForm.getExamForm().getName());
            examScheduleDTO.setStartDate(batchExamForm.getStartDate());
            examScheduleDTO.setEndDate(batchExamForm.getEndDate());

            if(!(examScheduleDTO.getStartDate().isBlank()) && 
                (LocalDateTime.parse( examScheduleDTO.getStartDate().replace("T"," "), dtf)).isAfter(formattedNow)){
                examScheduleDTO.setStatus("Upcoming");
            }
            else if (!(examScheduleDTO.getEndDate().isBlank()) && 
                    (LocalDateTime.parse( examScheduleDTO.getEndDate().replace("T"," "), dtf)).isAfter(formattedNow)){
                examScheduleDTO.setStatus("In Progress");
            }
            else if (!(examScheduleDTO.getEndDate().isBlank()) && 
                    formattedNow.isAfter(LocalDateTime.parse( examScheduleDTO.getEndDate().replace("T"," "), dtf))){
                examScheduleDTO.setStatus("Done");
            }        
            examScheduleDTOList.add(examScheduleDTO);
        }
        return examScheduleDTOList;
    }

    public void saveBathExamFrom(BatchExamForm bef){
        batchExamFormRepository.save(bef);
    }

    public BatchExamForm findById(int id){
        return batchExamFormRepository.getById(id);
    }

    public List<BatchExamForm> findByBatch_Id(int batchId){
        return batchExamFormRepository.findByDeleteStatusAndBatch_IdAndExamForm_DeleteStatus(false, batchId, false);
    }

}
