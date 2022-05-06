package ac.processor;

import soot.jimple.AssignStmt;
import soot.jimple.NewArrayExpr;
import jymbolic.entity.ContextMessage;
import jymbolic.entity.GlobalMessage;
import jymbolic.entity.value.heap.array.NewArrayHeapObject;
import jymbolic.execution.processor.rightop.expr.newex.INewArrayExprProcessor;

public class ThreadNewArrayExprProcessor implements INewArrayExprProcessor{

	private static ThreadNewArrayExprProcessor processor;

	private ThreadNewArrayExprProcessor() {
	}

	public static ThreadNewArrayExprProcessor v() {
		if (processor == null) {
			processor = new ThreadNewArrayExprProcessor();
		}
		return processor;
	}
	
	@Override
	public NewArrayHeapObject getNewArrayHeapObject(AssignStmt stmt, NewArrayExpr newArrayExpr,
			ContextMessage context, GlobalMessage globalMessage) {
		return null;
	}

}
