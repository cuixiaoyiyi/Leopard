/* AsyncDetecotr - an Android async component misuse detection tool
 * Copyright (C) 2018 Baoquan Cui
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package ac.checker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

/**
 * The loop summary of method
 * 
 * @author Baoquan Cui
 * @version 1.0
 */
public class RunMethod extends MethodLoopAnalyzer{

	protected static final Map<String, RunMethod> doInBackgroundMethods = new HashMap<>();

	protected Set<Unit> mCancelUnitList = new HashSet<>();

	public RunMethod(SootMethod methodUnderAnalysis) {
		super(methodUnderAnalysis);
		generation();
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
	
	protected void afterLoopAnalysis() {
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
		}
	}

}
