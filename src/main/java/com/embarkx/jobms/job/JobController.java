package com.embarkx.jobms.job;

import com.embarkx.jobms.job.dto.jobDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    @GetMapping("/jobs")
    public ResponseEntity<List<jobDTO>> findAll() {
        return ResponseEntity.ok(jobService.findAll());
    }

    @PostMapping("/jobs")
    public ResponseEntity<String>  createJob(@RequestBody Job job) {
        jobService.createJob(job);
        return  new ResponseEntity<>("job added successfully" ,HttpStatus.OK);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<jobDTO> getJobById(@PathVariable Long id) {

        jobDTO jobDTO = jobService.getJobById(id);
        if(jobDTO !=null){
            return  new ResponseEntity<>(jobDTO, HttpStatus.OK);
        }else{
            return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        boolean deleted = jobService.deleteJobById(id);
        if (deleted) {
            return new ResponseEntity<>("jobs get deletd", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("not found" ,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<String> updateJob(@PathVariable Long id, @RequestBody Job updatedJob){
    boolean updated = jobService.updateJob(id, updatedJob);
  if(updated){
      return new ResponseEntity<>("job updated sucessfully", HttpStatus.OK);
  }else{
      return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
  }
    }
}