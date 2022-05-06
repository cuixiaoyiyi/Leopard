package ac.processor;

import java.util.Map;

import soot.Local;
import soot.SootField;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticFieldRef;
import ac.ThreadMain;
import ac.entity.ThreadRefObject;
import ac.entity.ThreadStatus;
import ac.entity.ThreadTypeState;
import ac.record.ThreadErrorRecord;
import ac.util.ThreadInherit;
import jymbolic.entity.AbstractTypeState;
import jymbolic.entity.ContextMessage;
import jymbolic.entity.GlobalMessage;
import jymbolic.entity.value.BasicNullConstant;
import jymbolic.entity.value.IBasicValue;
import jymbolic.exception.AbstractAndrolicException;
import jymbolic.execution.ISymbolicEngineInstrumenter;
import jymbolic.util.Log;

public class ThreadSymbolicInstrumenter implements ISymbolicEngineInstrumenter{

	private static ThreadSymbolicInstrumenter processor;

	private ThreadSymbolicInstrumenter() {
	}

	public static ThreadSymbolicInstrumenter v() {
		if (processor == null) {
			processor = new ThreadSymbolicInstrumenter(); 
		}
		return processor;
	}
	
	@Override
	public void onPreStmtExecution(Unit currentUnit, GlobalMessage globalMessage) {
//		String str = "virtualinvoke $r0.<com.farmerbb.taskbar.activity.SelectAppActivity: void setContentView(int)>(2131492930)";
		String str = "$r9 = new com.farmerbb.taskbar.activity.SelectAppActivity$AppListGenerator";
		if (currentUnit.toString().contains(str)) {
			Log.e("find ");
		}
	}

	/**
	 * Only consider the situation that the right operator points to an AsyncTask object
	 * @param rightOp
	 * @param globalMessage
	 * @return
	 */
	private IBasicValue getRightOpValue(Value rightOp, GlobalMessage globalMessage) {
		ContextMessage context = globalMessage.getContextStack().peek();
		if (rightOp instanceof Local) {
			return context.getLocalToObject().getOrDefault(rightOp, null);
		}
		else if (rightOp instanceof InstanceFieldRef) {
			Value base = ((InstanceFieldRef) rightOp).getBase();
			SootField field = ((InstanceFieldRef) rightOp).getField();
			IBasicValue subordinateObject = context.getLocalToObject().get(base);
			if (subordinateObject == null || subordinateObject instanceof BasicNullConstant) {
				return null;
			}
			return (IBasicValue) globalMessage.getNonStaticFieldToObject().get(subordinateObject).get(field);
		}
		else if (rightOp instanceof StaticFieldRef) {
			return globalMessage.getStaticFieldToObject().getOrDefault(rightOp, null);
		}
		else {
			return null;
		}
	}
	
	@Override
	public void onPostStmtExecution(Unit currentUnit, GlobalMessage globalMessage) {
		if (currentUnit instanceof AssignStmt && 
				((AssignStmt) currentUnit).getLeftOp() instanceof FieldRef) { // mark AsyncTaskObject whose alias is a field
			Value leftOp = ((AssignStmt) currentUnit).getLeftOp();
			Value rightOp = ((AssignStmt) currentUnit).getRightOp();
			Type leftType = leftOp.getType();
			if (ThreadInherit.isInheritedFromThread(leftType)) {
				IBasicValue rightValue = this.getRightOpValue(rightOp, globalMessage);
				if (rightValue != null && rightValue instanceof ThreadRefObject) {
					ThreadRefObject threadObj = (ThreadRefObject) rightValue;
					threadObj.setAliasedToField(true);
					ThreadMain.rightInstanceMap.put(threadObj.getObjectKey(), threadObj);
				}
			}
		}
		if (globalMessage.getContextStack().isEmpty() && 
				(currentUnit instanceof ReturnStmt || currentUnit instanceof ReturnVoidStmt)) {
			Map<IBasicValue, AbstractTypeState> objectToTypeState = globalMessage.getObjectToTypeState();
			for (Map.Entry<IBasicValue, AbstractTypeState> entry: objectToTypeState.entrySet()) {
				if (entry.getKey() instanceof ThreadRefObject) {
					ThreadTypeState state = (ThreadTypeState) entry.getValue();
					if (state.getCurrentStatus() == ThreadStatus.RUNNING && !state.isInterrupted()) {
						ThreadErrorRecord.recordNTT((ThreadRefObject) entry.getKey(), globalMessage);
					}
				}
			}
		}
	}

	@Override
	public void onExceptionProcess(Unit currentUnit, GlobalMessage globalMessage, AbstractAndrolicException exception) {
		Map<IBasicValue, AbstractTypeState> objectToTypeState = globalMessage.getObjectToTypeState();
		for (Map.Entry<IBasicValue, AbstractTypeState> entry: objectToTypeState.entrySet()) {
			if (entry.getKey() instanceof ThreadRefObject) {
				ThreadTypeState state = (ThreadTypeState) entry.getValue();
				if (state.getCurrentStatus() == ThreadStatus.RUNNING && !state.isInterrupted()) {
					ThreadErrorRecord.recordNTT((ThreadRefObject) entry.getKey(), globalMessage);
				}
			}
		}
	}
}
