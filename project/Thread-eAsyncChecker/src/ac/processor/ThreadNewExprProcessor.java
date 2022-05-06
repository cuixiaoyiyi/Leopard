package ac.processor;

import soot.jimple.AssignStmt;
import soot.jimple.NewExpr;
import ac.entity.RunnableRefObject;
import ac.entity.ThreadRefObject;
import ac.entity.ThreadTypeState;
import ac.util.ThreadInherit;
import jymbolic.entity.ContextMessage;
import jymbolic.entity.GlobalMessage;
import jymbolic.entity.value.heap.ref.NewRefHeapObject;
import jymbolic.execution.processor.rightop.expr.newex.INewExprProcessor;

public class ThreadNewExprProcessor implements INewExprProcessor {

	private static ThreadNewExprProcessor processor;

	private ThreadNewExprProcessor() {

	}

	public static ThreadNewExprProcessor v() {
		if (processor == null) {
			processor = new ThreadNewExprProcessor();
		}
		return processor;
	}

	@Override
	public NewRefHeapObject getNewHeapObject(AssignStmt assignStmt, NewExpr newExpr, ContextMessage context,
			GlobalMessage globalMessage) {
		if (ThreadInherit.isInheritedFromThread(newExpr.getBaseType())) {
			ThreadRefObject theObject = new ThreadRefObject(assignStmt, globalMessage);
			ThreadTypeState newState = new ThreadTypeState();
			newState.getExecutionUnitList().add(assignStmt);
			globalMessage.getObjectToTypeState().put(theObject, newState);
			return theObject;
		} else if (ThreadInherit.isInheritedFromRunnable(newExpr.getBaseType())) {
			RunnableRefObject runnableRefObject = new RunnableRefObject(assignStmt, globalMessage);
			return runnableRefObject;
		} else {
			return null;
		}
	}

}
