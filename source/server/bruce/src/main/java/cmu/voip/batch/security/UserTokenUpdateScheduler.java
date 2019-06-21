package cmu.voip.batch.security;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class UserTokenUpdateScheduler extends QuartzJobBean {

	private static final Logger logger = LogManager.getLogger(UserTokenUpdateScheduler.class);

	private String jobName;
	private JobLauncher jobLauncher;
	private JobLocator jobLocator;

	public JobLauncher getJobLauncher() {
		return jobLauncher;
	}

	public void setJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher;
	}

	public JobLocator getJobLocator() {
		return jobLocator;
	}

	public void setJobLocator(JobLocator jobLocator) {
		this.jobLocator = jobLocator;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		Map mapData = context.getMergedJobDataMap();

		jobName = (String) mapData.get("jobName");

		try {

			JobExecution execution = jobLauncher.run(jobLocator.getJob(jobName), new JobParameters());
			logger.debug("Execution Status: " + execution.getStatus());
		} catch (Exception e) {
			logger.error("Encountered job execution exception! ");
			e.printStackTrace();
		}

	}

}
