/********************************
 *	프로젝트 : system.monitor
 *	패키지   :  com.kyj.plan
 *	작성일   :  2016. 2. 24.
 *	작성자   :  KYJ
 *******************************/
package com.kyj.bci.monitor;

/**
 * @author KYJ
 *
 */
public class ApplicationModel
{
	private String status;
	private Integer processId;
	private String applicationName;
	private String argument;
	private String jvmArgs;

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Integer getProcessId()
	{
		return processId;
	}

	public void setProcessId(Integer processId)
	{
		this.processId = processId;
	}

	public String getApplicationName()
	{
		return applicationName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public String getArgument()
	{
		return argument;
	}

	public void setArgument(String argument)
	{
		this.argument = argument;
	}

	public String getJvmArgs()
	{
		return jvmArgs;
	}

	public void setJvmArgs(String jvmArgs)
	{
		this.jvmArgs = jvmArgs;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicationModel other = (ApplicationModel) obj;
		if (processId == null)
		{
			if (other.processId != null)
				return false;
		} else if (!processId.equals(other.processId))
			return false;
		return true;
	}

}
