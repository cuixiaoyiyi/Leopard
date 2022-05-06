package ac.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import ac.constant.Signature;
import soot.ArrayType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;

public class InheritanceProcess {

	public enum MatchType {
		equal, regular
	}

	public static List<SootClass> getDirectSubClasses(SootClass currentClass) {
		List<SootClass> subclasses = new ArrayList<SootClass>();
		for (SootClass theClass : Scene.v().getApplicationClasses()) {
			if (theClass.getSuperclassUnsafe().equals(currentClass)) {
				subclasses.add(theClass);
			}
		}
		return subclasses;
	}

	public static boolean isInheritedFromGivenClass(Type subType, Type parentType) {
		if (subType instanceof PrimType || parentType instanceof PrimType)
			return false;
		else if ((subType instanceof ArrayType && parentType instanceof RefType)
				|| (parentType instanceof ArrayType && subType instanceof RefType))
			return false;
		else if (subType instanceof RefType && parentType instanceof RefType)
			return isInheritedFromGivenClass(((RefType) subType).getSootClass(), parentType.toString(),
					MatchType.equal);
		else if (subType instanceof ArrayType && parentType instanceof ArrayType) {
			int subDim = ((ArrayType) subType).numDimensions;
			int parentDim = ((ArrayType) parentType).numDimensions;
			if (subDim != parentDim)
				return false;
			Type subBaseType = ((ArrayType) subType).baseType;
			Type parentBaseType = ((ArrayType) parentType).baseType;
			return isInheritedFromGivenClass(subBaseType, parentBaseType);
		} else {
			return false;
		}
	}

	public static boolean isInheritedFromThread(SootClass theClass) {
//		if(theClass.hasSuperclass()) {
//			if ("java.lang.Object".equals(theClass.getSuperclass().getName())) {
//				return isInheritedFromGivenClass(theClass, ClassSignature.ASYNC_TASK, MatchType.equal);
//			}
//		}
		return isInheritedFromGivenClass(theClass, Signature.CLASS_THREAD, MatchType.equal);
	}

	public static boolean isInheritedFromActivity(SootClass theClass) {
		return isInheritedFromGivenClass(theClass, Signature.CLASS_ACTIVITY, MatchType.equal);
	}

	public static boolean isInheritedFromView(SootClass theClass) {
		return isInheritedFromGivenClass(theClass, Signature.CLASS_VIEW, MatchType.equal);
	}
	
	public static boolean isInheritedFromGivenClass(SootClass theClass, String classNameUnderMatch) {
		MatchType matchType = MatchType.equal;
		if (theClass == null)
			return false;
		if (isTypeMatch(theClass, classNameUnderMatch, matchType)) {
			return true;
		}
		if(theClass.resolvingLevel() < SootClass.HIERARCHY) {
			Scene.v().addBasicClass(theClass.getName(), SootClass.HIERARCHY);
		}
		if(theClass.resolvingLevel() < SootClass.HIERARCHY ) {
			return false;
		}
		for (SootClass interfaceClass : theClass.getInterfaces()) {
			if (isInheritedFromGivenClass(interfaceClass, classNameUnderMatch, matchType)) {
				return true;	
			}
		}
		return isInheritedFromGivenClass(theClass.getSuperclassUnsafe(), classNameUnderMatch, matchType);
	}

	public static boolean isInheritedFromGivenClass(SootClass theClass, String classNameUnderMatch,
			MatchType matchType) {
		if (theClass == null)
			return false;
		if(theClass.resolvingLevel() < SootClass.HIERARCHY) {
			Scene.v().addBasicClass(theClass.getName(), SootClass.HIERARCHY);
		}
		SootClass parentClass = Scene.v().getSootClass(classNameUnderMatch);
		if( parentClass.resolvingLevel() < SootClass.HIERARCHY) {
			Scene.v().addBasicClass(parentClass.getName(), SootClass.HIERARCHY);
		}
		if(theClass.resolvingLevel() < SootClass.HIERARCHY ||  parentClass.resolvingLevel() < SootClass.HIERARCHY) {
			return false;
		}
		if (isTypeMatch(theClass, classNameUnderMatch, matchType))
			return true;
		for (SootClass interfaceClass : theClass.getInterfaces()) {
			if (isInheritedFromGivenClass(interfaceClass, classNameUnderMatch, matchType)) {
				return true;
			}
		}	
		return isInheritedFromGivenClass(theClass.getSuperclassUnsafe(), classNameUnderMatch, matchType);
	}

	private static boolean isTypeMatch(SootClass currentClass, String classNameUnderMatch, MatchType matchType) {
		if (matchType == MatchType.equal && currentClass.getType().toString().equals(classNameUnderMatch))
			return true;
		else if (matchType == MatchType.regular
				&& isRegularMatch(currentClass.getType().toString(), classNameUnderMatch))
			return true;
		else
			return false;
	}

	private static boolean isRegularMatch(String targetStr, String regularStr) {
		Pattern p = Pattern.compile(regularStr);
		Matcher m = p.matcher(targetStr);
		return m.matches();
	}

	public static boolean isInheritedFromFragment(SootClass theClass) {
		return isInheritedFromGivenClass(theClass, Signature.CLASS_FRAGMENT, MatchType.equal)
				|| isInheritedFromGivenClass(theClass, Signature.CLASS_SUPPORT_FRAGMENT, MatchType.equal)
				|| isInheritedFromGivenClass(theClass, Signature.CLASS_SUPPORT_FRAGMENT_V7, MatchType.equal);
	}

	/**
	 * Checks whether the given class name belongs to a system package
	 * 
	 * @param className The class name to check
	 * @return True if the given class name belongs to a system package, otherwise
	 *         false
	 */
	public static boolean isClassInSystemPackage(String className) {
		return className.startsWith("android.") || className.startsWith("java.") || className.startsWith("javax.")
				|| className.startsWith("sun.") || className.startsWith("org.omg.")
				|| className.startsWith("org.w3c.dom.") || className.startsWith("com.google.")
				|| className.startsWith("com.android.") || className.startsWith("com.ibm.")
				|| className.startsWith("com.sun.") || className.startsWith("com.apple.")
				|| className.startsWith("org.w3c.") || className.startsWith("soot");
	}

//	public static boolean isInheritedFromFragment(SootClass theClass) {
//		return isInheritedFromGivenClass(theClass, ClassSignature.FRAGMENTCLASS,MatchType.equal) || 
//				isInheritedFromGivenClass(theClass, ClassSignature.SUPPORTFRAGMENTCLASS_V7,MatchType.equal) ||
//				isInheritedFromGivenClass(theClass, ClassSignature.SUPPORTFRAGMENTCLASS,MatchType.equal) ;
//	}
}
