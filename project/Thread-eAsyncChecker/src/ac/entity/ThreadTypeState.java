package ac.entity;

import java.util.ArrayList;
import java.util.List;

import jymbolic.entity.AbstractTypeState;
import soot.Unit;

public class ThreadTypeState extends AbstractTypeState {
	private ThreadStatus currentStatus = ThreadStatus.PENDING;
    
    /**
     * Thread can only be executed once
     */
    private boolean isStart = false;
    
    private boolean isInterrupted = false;
    
    private List<Unit> executionUnitList = new ArrayList<Unit>();
    
	public List<Unit> getExecutionUnitList() {
		return executionUnitList;
	}

	public void setExecutionUnitList(List<Unit> executionUnitList) {
		this.executionUnitList = executionUnitList;
	}

	public ThreadStatus getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(ThreadStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	public boolean isInterrupted() {
		return isInterrupted;
	}

	public void setInterrupted(boolean isCancelled) {
		this.isInterrupted = isCancelled;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setExecuted(boolean isExecuted) {
		this.isStart = isExecuted;
	}

	@Override
	public Object clone() {
		ThreadTypeState cloneObject = new ThreadTypeState();
		cloneObject.isInterrupted = this.isInterrupted;
		cloneObject.isStart = this.isStart;
		cloneObject.currentStatus = this.currentStatus;
		cloneObject.executionUnitList.addAll(this.executionUnitList);
		return cloneObject;
	}
}
