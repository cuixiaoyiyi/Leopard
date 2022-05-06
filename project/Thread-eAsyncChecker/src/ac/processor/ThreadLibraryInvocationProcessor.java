package ac.processor;

import java.lang.reflect.InvocationTargetException;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import ac.constant.Signature;
import ac.entity.RunnableRefObject;
import ac.entity.ThreadRefObject;
import ac.entity.ThreadStatus;
import ac.entity.ThreadTypeState;
import ac.util.ThreadInherit;
import ac.util.ThreadOperationChecker;
import jymbolic.entity.ContextMessage;
import jymbolic.entity.GlobalMessage;
import jymbolic.entity.value.IBasicValue;
import jymbolic.entity.value.numeric.BasicNumericConstant;
import jymbolic.exception.AbstractAndrolicException;
import jymbolic.execution.processor.library.proxy.ILibraryInvocationProcessor;
import jymbolic.util.Log;

public class ThreadLibraryInvocationProcessor implements ILibraryInvocationProcessor {

	private static ThreadLibraryInvocationProcessor processor;

	private ThreadLibraryInvocationProcessor() {
	}

	public static ThreadLibraryInvocationProcessor v() {
		if (processor == null) {
			processor = new ThreadLibraryInvocationProcessor();
		}
		return processor;
	}

	@Override
	public IBasicValue getLibraryInvocationReturnValue(AssignStmt stmt, InvokeExpr libraryInvokeExpr,
			ContextMessage context, GlobalMessage globalMessage)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ClassNotFoundException, CloneNotSupportedException, AbstractAndrolicException {
		return this.processLibraryInvokeExpr(stmt, libraryInvokeExpr, context, globalMessage);
	}

	@Override
	public boolean processLibraryInvocation(InvokeStmt stmt, InvokeExpr libraryInvokeExpr, ContextMessage context,
			GlobalMessage globalMessage)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, AbstractAndrolicException {
		if (this.processLibraryInvokeExpr(stmt, libraryInvokeExpr, context, globalMessage) != null)
			return true;
		else
			return false;
	}

	private IBasicValue processLibraryInvokeExpr(Unit unit, InvokeExpr libraryInvokeExpr, ContextMessage context,
			GlobalMessage globalMessage) throws AbstractAndrolicException {
		if (libraryInvokeExpr instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) libraryInvokeExpr;
			IBasicValue threadObj = context.getLocalToObject().get(instanceInvokeExpr.getBase());
//			String signature = instanceInvokeExpr.getMethod().getSignature();
			if (ThreadInherit.isStartInvokeExpr(libraryInvokeExpr)) {
				Value value = null;
				value = instanceInvokeExpr.getBase();
				threadObj = context.getLocalToObject().get(value);
				Local base = (Local) value;
				IBasicValue b = null;
				try {
					b = this.startThread((ThreadRefObject) threadObj, base, unit, context, globalMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return b;
			}

			if (threadObj instanceof ThreadRefObject) {
				if (instanceInvokeExpr.getMethod().isConstructor()
						&& Signature.CLASS_THREAD.equals(instanceInvokeExpr.getMethod().getDeclaringClass().getName())
						&& instanceInvokeExpr.getArgCount() > 0
						&& ThreadInherit.isInheritedFromRunnable(instanceInvokeExpr.getArg(0).getType())) {
					RunnableRefObject rightValue = (RunnableRefObject) context.getLocalToObject().get(instanceInvokeExpr.getArg(0));
					((ThreadRefObject) threadObj).setTargetRunnableField(rightValue);
					return null;
				}
				String subSignature = libraryInvokeExpr.getMethod().getSubSignature();
				ThreadTypeState state = ((ThreadRefObject) threadObj).getTypeState(globalMessage);
				Local base = (Local) ((InstanceInvokeExpr) libraryInvokeExpr).getBase();
				switch (subSignature) {
				case Signature.METHOD_SUBSIG_INTERRUPT:
				case Signature.METHOD_SUBSIG_INTERRUPT_SAFELY:
					this.interruptThread((ThreadRefObject) threadObj, base, unit, context, globalMessage);
					return new BasicNumericConstant(IntConstant.v(1));
				case Signature.METHOD_SUBSIG_IS_INTERRUPTED:
					return state.isInterrupted() ? new BasicNumericConstant(IntConstant.v(1))
							: new BasicNumericConstant(IntConstant.v(0));
				default:
					return null;

				}

			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private ThreadRefObject startThread(ThreadRefObject threadObj, Local base, Unit unit,
			ContextMessage context, GlobalMessage globalMessage) throws AbstractAndrolicException {
		ThreadTypeState state = threadObj.getTypeState(globalMessage);
		state.getExecutionUnitList().add(unit);
		state.setCurrentStatus(ThreadStatus.RUNNING);
		state.setExecuted(true);

		ThreadOperationChecker.checkHTR(threadObj, unit, globalMessage);
		ThreadOperationChecker.checkINR(threadObj, unit, globalMessage);

		return threadObj;
	}

	private boolean interruptThread(ThreadRefObject threadObj, Local base, Unit unit, ContextMessage context,
			GlobalMessage globalMessage) throws AbstractAndrolicException {
		ThreadTypeState state = threadObj.getTypeState(globalMessage);
		if (state == null) {
			Log.e("************");
			for (int i = 0; i < globalMessage.getContextStack().size(); i++) {
				Log.e(globalMessage.getContextStack().get(i).getMethod().getSignature());
			}
			Log.e(unit);
			Log.e(globalMessage, "-", threadObj);
		}
		state.getExecutionUnitList().add(unit);
		state.setCurrentStatus(ThreadStatus.CANCEL);
		state.setInterrupted(true);
		return true;
	}
}
