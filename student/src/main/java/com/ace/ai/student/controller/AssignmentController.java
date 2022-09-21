package com.ace.ai.student.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ace.ai.student.datamodel.Assignment;
import com.ace.ai.student.datamodel.Student;
import com.ace.ai.student.datamodel.StudentAssignmentMark;
import com.ace.ai.student.dtomodel.AssignmentDateTimeDTO;
import com.ace.ai.student.dtomodel.AssignmentFileDTO;
import com.ace.ai.student.dtomodel.AssignmentMarkDTO;
import com.ace.ai.student.repository.StudentAssignmentMarkRepository;
import com.ace.ai.student.service.AssignmentService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/student")
@Slf4j
public class AssignmentController {

    @Autowired
    AssignmentService assignmentService;
    @Autowired
    StudentAssignmentMarkRepository studentAssignmentMarkRepository;

    @GetMapping("/assignmentView")
    public ModelAndView assignmentStudent(@RequestParam("assignmentId") Integer assignmentId,@RequestParam("studentId") Integer studentId,ModelMap model) throws ParseException{
        AssignmentDateTimeDTO assignmentDateTimeDTO = assignmentService.getDateTimeByAssignmentId(assignmentId);
        AssignmentMarkDTO assignmentMarkDTO= assignmentService.getStudentMarkByAssiIdAndStuId(assignmentId,studentId);
        String status = assignmentService.getStatusAssignment(assignmentId);
        model.addAttribute("assignmentDateTimeDTO" ,assignmentDateTimeDTO);
        model.addAttribute("assignmentMarkDTO", assignmentMarkDTO);
        AssignmentFileDTO assignmentFileDTO = new AssignmentFileDTO();
        assignmentFileDTO.setAssignmentId(assignmentId);
        assignmentFileDTO.setStudentId(studentId);
        model.addAttribute("status", status);
        return new ModelAndView("S001-03","assignmentFileDTO",assignmentFileDTO);
    }

    @PostMapping("/assignmentAdd")
    public String assignmentAdd(@ModelAttribute("assignmentFileDTO") AssignmentFileDTO assignmentFileDTO, ModelMap model) throws ParseException{
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = localDate.format(dateFormatter);
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String currentTime = localTime.format(timeFormatter);
        String submitTime = assignmentService.englishTime(currentTime);
        StudentAssignmentMark  studentAssignmentMark = new StudentAssignmentMark();
        String fileName=StringUtils.cleanPath(assignmentFileDTO.getAssignmentFile().getOriginalFilename());
        studentAssignmentMark.setUploadedFile(fileName);
        studentAssignmentMark.setDate(currentDate);
        studentAssignmentMark.setTime(submitTime);
        Assignment assignment = new Assignment();
        // log.info("assignmentid -->"+assignmentFileDTO.getAssignmentId());
        assignment.setId(assignmentFileDTO.getAssignmentId());
        studentAssignmentMark.setAssignment(assignment);
        Student student =  new Student();
        student.setId(assignmentFileDTO.getStudentId());
        studentAssignmentMark.setStudent(student);
        StudentAssignmentMark studentAssignmentMarkSaved = studentAssignmentMarkRepository.save(studentAssignmentMark);
        String uploadDir = "./assets/img/"+studentAssignmentMarkSaved.getId();
        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            try {
              Files.createDirectories(uploadPath);
            } catch (IOException e) {
              
              e.printStackTrace();
            }
            }
          try( InputStream inputStream=assignmentFileDTO.getAssignmentFile().getInputStream()){
            Path filePath=uploadPath.resolve(fileName);
            System.out.println(filePath.toFile().getAbsolutePath());
            Files.copy(inputStream, filePath ,StandardCopyOption.REPLACE_EXISTING);
          }catch (IOException e){
              try {
                throw new IOException("Could not save upload file: " + fileName);
              } catch (IOException e1) {
                
                e1.printStackTrace();
              }
          } 
          AssignmentDateTimeDTO assignmentDateTimeDTO = assignmentService.getDateTimeByAssignmentId(assignmentFileDTO.getAssignmentId());
          AssignmentMarkDTO assignmentMarkDTO= assignmentService.getStudentMarkByAssiIdAndStuId(assignmentFileDTO.getAssignmentId(),assignmentFileDTO.getStudentId());
          assignmentMarkDTO.setSubmitDate(currentDate);
          assignmentMarkDTO.setSubmitTime(currentTime);
          String status = assignmentService.getStatusAssignment(assignmentFileDTO.getAssignmentId());
          model.addAttribute("assignmentDateTimeDTO" ,assignmentDateTimeDTO);
          model.addAttribute("assignmentMarkDTO", assignmentMarkDTO);
          model.addAttribute("status", status);
          return "S001-03";
          
         
    }

    
}
