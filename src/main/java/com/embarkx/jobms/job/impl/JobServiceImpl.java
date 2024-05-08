package com.embarkx.jobms.job.impl;


import com.embarkx.jobms.job.Job;
import com.embarkx.jobms.job.JobRepository;
import com.embarkx.jobms.job.JobService;
import com.embarkx.jobms.job.clients.CompanyClient;
import com.embarkx.jobms.job.clients.ReviewClient;
import com.embarkx.jobms.job.dto.jobDTO;
import com.embarkx.jobms.job.external.Company;
import com.embarkx.jobms.job.external.Review;
import com.embarkx.jobms.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

  // private List<Job> jobs = new ArrayList<>();
   JobRepository jobRepository;

   private CompanyClient companyClient;
   private ReviewClient reviewClient;

   int attempt=0;

   @Autowired
   RestTemplate restTemplate;


  public JobServiceImpl(JobRepository jobRepository,CompanyClient companyClient,ReviewClient reviewClient){
       this.jobRepository = jobRepository;
       this.companyClient= companyClient;
       this.reviewClient = reviewClient;

   }
    @Override
//    @CircuitBreaker(name = "companyBreaker",
//            fallbackMethod = "companyBreakerFallback")


//    @Retry(name = "companyBreaker",
//        fallbackMethod = "companyBreakerFallback")


    @RateLimiter(name = "companyBreaker")
    public List<jobDTO> findAll() {
      System.out.println("Attempt:"+ ++attempt);
      List<Job> jobs = jobRepository.findAll();
      List<jobDTO> jobDTOS = new ArrayList<>();

          return  jobs.stream().map(this::converToDto)
                  .collect(Collectors.toList());

      }
      public List<String> companyBreakerFallback(Exception e){
        List<String> list = new ArrayList<>();
        list.add("Vrushabh");
        return  list;
      }
      private jobDTO converToDto(Job job) {
       Company company = companyClient.getCompany(job.getCompanyId());
      List<Review> reviews = reviewClient.getReviews(job.getCompanyId());

          jobDTO jobDTO = JobMapper.mapToJobWithCompanyDto(job , company,reviews);
          //jobDTO.setCompany(company);
          return jobDTO;
      }

    @Override
    public void createJob(Job job) {

        jobRepository.save(job);
    }

    @Override
    public jobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        return converToDto(job);

    }

    @Override
    public boolean deleteJobById(Long id) {
      try{
          jobRepository.deleteById(id);
          return true;
      }catch (Exception e){
          e.printStackTrace();
          return false;
      }
  }

    @Override
    public boolean updateJob(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);

            if(jobOptional.isPresent()){
                Job job = jobOptional.get();
              job.setTitle(updatedJob.getTitle());
              job.setDescription(updatedJob.getDescription());
              job.setLocation(updatedJob.getLocation());
              job.setMaxSalary(updatedJob.getMaxSalary());
              job.setMinSalary(updatedJob.getMinSalary());
              jobRepository.save(job);
              return true;
            }
        return false;
    }
}