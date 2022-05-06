package ac.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class RunMethod {

	private static final Map<String, RunMethod> doInBackgroundMethods = new HashMap<>();

	private List<UnitInfo> mUnitInfos = new ArrayList<UnitInfo>();

	private Set<Unit> mLoopHeaderList = new HashSet<>();

	private Set<Unit> mCancelUnitList = new HashSet<>();

	private UnitGraph mUnitGraph = null;

	public RunMethod(SootMethod methodUnderAnalysis) {
		if (methodUnderAnalysis.hasActiveBody()) {
			mUnitGraph = new BriefUnitGraph(methodUnderAnalysis.getActiveBody());
			generation();
		}
	}

	public Set<Unit> getLoopStartUnits() {
		return mLoopHeaderList;
	}

	public Set<Unit> getCancelledUnits() {
		return mCancelUnitList;
	}

	public boolean isAllLoopCancelled() {
		for (Unit unit : mCancelUnitList) {
			UnitInfo unitInfo = getUnitInfo(unit);
			if (unit instanceof GotoStmt) {
				GotoStmt gotoStmt = (GotoStmt) unit;
				Unit targetUnit = gotoStmt.getTarget();
				UnitInfo targetInfo = getUnitInfo(targetUnit);
				if (unitInfo.mLoopHeaderUnit != null) {
					UnitInfo headerUnitInfo = getUnitInfo(unitInfo.mLoopHeaderUnit);
					headerUnitInfo.isCancelled = targetInfo.mLoopHeaderUnit != unitInfo.mLoopHeaderUnit;
				}
			}
			if (mLoopHeaderList.contains(unit)) {
				UnitInfo headerUnitInfo = getUnitInfo(unit);
				headerUnitInfo.isCancelled = true;
			}
		}
		for (Unit unit : mLoopHeaderList) {
			UnitInfo unitInfo = getUnitInfo(unit);
			if (!unitInfo.isCancelled) {
				return false;
			}
		}
		return true;
	}

	protected void generation() {

		for (Unit head : mUnitGraph.getHeads()) {
			traverUnitGraph(head, 0);
		}

		for (UnitInfo unitInfo : mUnitInfos) {
			mLoopHeaderList.add(unitInfo.mLoopHeaderUnit);
		}

		for (Unit unit : mUnitGraph) {

			InvokeExpr theExpr = ((Stmt) unit).containsInvokeExpr() ? ((Stmt) unit).getInvokeExpr() : null;
			// Process the summary of invoked method
			if (theExpr != null && theExpr.getMethod().hasActiveBody()) {
				String key = theExpr.getMethod().getSignature();
				RunMethod currentMethodSummary = doInBackgroundMethods.get(key);
				if (currentMethodSummary != null) {
					this.mLoopHeaderList.addAll(currentMethodSummary.mLoopHeaderList);
					this.mCancelUnitList.addAll(currentMethodSummary.mCancelUnitList);
				}
			}
			if (unit instanceof GotoStmt) {
				mCancelUnitList.add(unit);
			}

//			if (unit instanceof AssignStmt) {
//				AssignStmt jAssignStmt = (AssignStmt) unit;
//				if (jAssignStmt.containsInvokeExpr()) {
//					InvokeExpr invokeExpr = jAssignStmt.getInvokeExpr();
//
//					SootMethod sootMethod = invokeExpr.getMethod();
//					if (InheritanceProcess.isInheritedFromAsyncTask(sootMethod.getDeclaringClass())
//							&& sootMethod.getName().contains(MethodSignature.IS_CANCELLED_SUBSIG)) {
//						// cancelledCount++;
//						mCancelUnitList.add(unit);
//					}
//				}
//			}
		}

	}

	private Unit traverUnitGraph(Unit b0, int deepFirstSearchPathPosition) {
		// return: innermost loop header of b0
		UnitInfo unitInfo = getUnitInfo(b0);
		unitInfo.visited = true;
		unitInfo.mDeepFirstSearchPathPosition = deepFirstSearchPathPosition;
		for (Unit b : mUnitGraph.getSuccsOf(b0)) {
			UnitInfo unitInfob = getUnitInfo(b);
			if (!unitInfob.visited) {
				// case(A)
				Unit nh = traverUnitGraph(b, deepFirstSearchPathPosition + 1);
				tagLoopHeader(b0, nh);
			} else {
				if (unitInfob.mDeepFirstSearchPathPosition > 0) {
					// case(B)
					unitInfob.visited = true;
					tagLoopHeader(b0, b);
				} else if (unitInfob.mLoopHeaderUnit == null) {
					// case(C)

				} else {
					Unit h = unitInfob.mLoopHeaderUnit;
					UnitInfo unitInfoH = getUnitInfo(h);
					if (unitInfoH.mDeepFirstSearchPathPosition > 0) {
						// case(D)
						tagLoopHeader(b0, h);
					} else {
						// case(E) re-entry
						while (unitInfoH.mLoopHeaderUnit != null) {
							unitInfoH = getUnitInfo(unitInfoH.mLoopHeaderUnit);
							if (unitInfoH.mDeepFirstSearchPathPosition > 0) {
								tagLoopHeader(b0, h);
								break;
							}
						}
					}
				}
			}
		}
		unitInfo.mDeepFirstSearchPathPosition = 0;
		return unitInfo.mLoopHeaderUnit;
	}

	private void tagLoopHeader(Unit b, Unit h) {
		if (b == h || h == null) {
			return;
		}
		UnitInfo cur1 = getUnitInfo(b);
		UnitInfo cur2 = getUnitInfo(h);
		while (cur1.mLoopHeaderUnit != null) {
			UnitInfo ih = getUnitInfo(cur1.mLoopHeaderUnit);
			if (ih == cur2) {
				return;
			}
			if (ih.mDeepFirstSearchPathPosition < cur2.mDeepFirstSearchPathPosition) {
				cur1.mLoopHeaderUnit = cur2.mUnit;
				cur1 = cur2;
				cur2 = ih;
			} else {
				cur1 = ih;
			}
		}
		cur1.mLoopHeaderUnit = cur2.mUnit;

	}

	private UnitInfo getUnitInfo(Unit unit) {
		for (UnitInfo unitInfo : mUnitInfos) {
			if (unitInfo.mUnit.equals(unit)) {
				return unitInfo;
			}
		}
		UnitInfo unitInfo = new UnitInfo();
		unitInfo.mUnit = unit;
		mUnitInfos.add(unitInfo);
		return unitInfo;
	}

	/**
	 *  
	 */
	class UnitInfo {
		private Unit mUnit = null;
		private boolean visited = false;
		private int mDeepFirstSearchPathPosition = 0;
		private Unit mLoopHeaderUnit = null;
		private boolean isCancelled = false;

		@Override
		public boolean equals(Object obj) {
			boolean result = mUnit.equals(obj instanceof UnitInfo ? ((UnitInfo) obj).mUnit : obj);
			return result;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("unit-->" + mUnit);
			sb.append("\n");
			sb.append("iloop_header-->" + mLoopHeaderUnit);
			sb.append("\n");
			sb.append("DFEP_pos-->" + mDeepFirstSearchPathPosition);
			return sb.toString();
		}

	}

}
