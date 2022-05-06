package ac.entity;

import java.util.HashMap;
import java.util.Map;

import ac.constant.Signature;
import jymbolic.entity.GlobalMessage;
import jymbolic.entity.value.IBasicValue;
import jymbolic.entity.value.heap.ref.NewRefHeapObject;
import jymbolic.util.ClassInheritanceProcess;
import jymbolic.util.ClassInheritanceProcess.MatchType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.jimple.Stmt;

public class RunnableRefObject extends NewRefHeapObject {
	
	protected String objectKey = "";
	
	private boolean isAliasedToField = false;
	
	public String getObjectKey() {
		return objectKey;
	}
	
	public boolean isAliasedToField() {
		return isAliasedToField;
	}

	public void setAliasedToField(boolean isAliasedToField) {
		this.isAliasedToField = isAliasedToField;
	}

	public RunnableRefObject(Stmt initStatement, GlobalMessage globalMessage) {
		super(initStatement);
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < globalMessage.getContextStack().size(); i++) {
			SootMethod sootMethod = globalMessage.getContextStack().get(i).getMethod();
//			String signature = sootMethod.getName();
			String signature = sootMethod.getSignature();
			sb.append(signature + "--");
		}
		sb.append(initStatement.hashCode() + "--");
		sb.append(initStatement.toString());
		this.objectKey = sb.toString();
	}
	
	public Map<SootField, IBasicValue> getHoldingStrongReferenceField(GlobalMessage globalMessage) {
		Map<SootField, IBasicValue> fieldToValue = globalMessage.getNonStaticFieldToObject().getOrDefault(this, new HashMap<SootField, IBasicValue>());
		Map<SootField, IBasicValue> resultMap = new HashMap<SootField, IBasicValue>();
		for (Map.Entry<SootField, IBasicValue> entry: fieldToValue.entrySet()) {
			SootClass fieldClass = Scene.v().getSootClassUnsafe(entry.getKey().getType().toString());
			if (this.isInheritedFromView(fieldClass) || ClassInheritanceProcess.isInheritedFromActivity(fieldClass)
					|| this.isInheritedFromFragment(fieldClass) ) {
				resultMap.put(entry.getKey(), entry.getValue());
			}
		}
		return resultMap;
	}
	
	private boolean isInheritedFromView(SootClass sc) {
		return ClassInheritanceProcess.isInheritedFromGivenClass(sc, Signature.CLASS_VIEW, MatchType.equal);
	}
	
	private boolean isInheritedFromFragment(SootClass sc) {
		return ClassInheritanceProcess.isInheritedFromGivenClass(sc, Signature.CLASS_FRAGMENT, MatchType.equal) ||
				ClassInheritanceProcess.isInheritedFromGivenClass(sc, Signature.CLASS_SUPPORT_FRAGMENT, MatchType.equal);
	}



}
