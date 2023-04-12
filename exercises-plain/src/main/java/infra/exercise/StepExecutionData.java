package infra.exercise;

import java.util.Date;

import javax.validation.constraints.Min;

import org.springframework.batch.core.BatchStatus;

public class StepExecutionData {
    private long stepExecutionId;
    private String stepName;
    private BatchStatus status;
    @Min(value = 1)
    private int readCount;
    private int writeCount;
    private Date startTime;
    private Date endTime;
    private String exitCode;

    public long getStepExecutionId() {
        return stepExecutionId;
    }

    public void setStepExecutionId(long id) {
        this.stepExecutionId = id;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public void setStatus(BatchStatus status) {
        this.status = status;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(int writeCount) {
        this.writeCount = writeCount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getExitCode() {
        return exitCode;
    }

    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public String toString() {
        return "StepExecutionData [stepExecutionId=" + stepExecutionId + ", stepName=" + stepName + ", status=" + status + ", readCount=" + readCount
                + ", writeCount=" + writeCount + ", startTime=" + startTime + ", endTime=" + endTime + ", exitCode=" + exitCode + "]";
    }
}
