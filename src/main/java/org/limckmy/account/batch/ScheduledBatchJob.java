package org.limckmy.account.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledBatchJob implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job csvProcessingJob;

    @Value("${batch.job.run-immediately:false}")
    private boolean runImmediately;

    public ScheduledBatchJob(JobLauncher jobLauncher, Job csvProcessingJob) {
        this.jobLauncher = jobLauncher;
        this.csvProcessingJob = csvProcessingJob;
    }

    @Scheduled(cron = "${spring.batch.job.cron}")
    public void runJob() throws Exception {
        jobLauncher.run(csvProcessingJob, new JobParameters());
    }

    @Override
    public void run(String... args) throws Exception {
        if (runImmediately) {
            jobLauncher.run(csvProcessingJob, new JobParameters());
        }
    }
}