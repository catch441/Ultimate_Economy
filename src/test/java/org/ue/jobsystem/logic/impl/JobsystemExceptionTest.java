package org.ue.jobsystem.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.jobsystem.logic.api.JobsystemException;

@ExtendWith(MockitoExtension.class)
public class JobsystemExceptionTest {

	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void constructorTest() {
		when(messageWrapper.getErrorString("job_not_exist_in_jobcenter", "param1", 2)).thenReturn("my error message");
		JobsystemException e = new JobsystemException(messageWrapper, ExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER,
				"param1", 2);
		
		assertEquals(ExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER, e.getKey());
		assertEquals(2, e.getParams().length);
		assertEquals("param1", e.getParams()[0]);
		assertEquals(2, e.getParams()[1]);
		assertEquals("my error message", e.getMessage());
	}
}
