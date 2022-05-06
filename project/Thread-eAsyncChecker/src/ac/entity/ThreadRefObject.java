package ac.entity;

import soot.jimple.Stmt;
import jymbolic.entity.GlobalMessage;

public class ThreadRefObject extends RunnableRefObject {

	private RunnableRefObject targetRunnableField = null;

	public ThreadRefObject(Stmt initStatement, GlobalMessage globalMessage) {
		super(initStatement, globalMessage);
	}

	public ThreadTypeState getTypeState(GlobalMessage globalMessage) {
		return (ThreadTypeState) globalMessage.getObjectToTypeState().get(this);
	}

	public RunnableRefObject getTargetRunnableField() {
		return targetRunnableField;
	}

	public void setTargetRunnableField(RunnableRefObject targetRunnableField) {
		this.targetRunnableField = targetRunnableField;
	}

}
