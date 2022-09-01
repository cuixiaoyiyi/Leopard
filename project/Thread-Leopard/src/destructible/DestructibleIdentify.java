package destructible;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.options.Options;
import soot.tagkit.AbstractHost;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.constant.Signature;
import ac.util.Log;

public class DestructibleIdentify {

	public final static Map<SootClass, SootMethod> destructibleClasses = new HashMap<>();
	public final static Set<String> destroyClasses = new HashSet<>();
	public final static Set<String> closeClasses = new HashSet<>();
	public final static Set<String> annotationClasses = new HashSet<>();
	public final static Map<String, SootMethod> emptyDestructibleClasses = new HashMap<>();

	public static void collect() {
		Set<SootClass> classes = new HashSet<SootClass>();
		classes.addAll(Scene.v().getClasses());
		classes.addAll(Scene.v().getApplicationClasses());
		for (SootClass sootClass : classes) {
			if (isDestructibleClass(sootClass,sootClass)) {
			} 
		}
	}
	
	public static boolean isEmptyMethod(SootMethod sootMethod) {
		if(sootMethod.isAbstract()) {
			return false;
		}
		if(sootMethod.isNative()) {
			return false;
		}
		if(sootMethod.isStatic()) {
			return true;
		}
		if(!sootMethod.hasActiveBody()) {
			return true;
		}
		Body body = sootMethod.getActiveBody();
		if (body.getUnits().isEmpty()) {
			return true;
		}
		UnitGraph unitGraph = new BriefUnitGraph(body);
		Local thisLocal = body.getThisLocal();
		SimpleLocalDefs localDefs = new SimpleLocalDefs(unitGraph);
		SimpleLocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);
		for (Unit defsOfThisLocal : localDefs.getDefsOf(thisLocal)) {
			if(!localUses.getUsesOf(defsOfThisLocal).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDestructibleClass(SootClass originClass, SootClass sootClass) {
		if (destructibleClasses.containsKey(sootClass)) {
			destructibleClasses.put(originClass, destructibleClasses.get(sootClass)); 
			return true;
		}
		if(Signature.CLASS_THREAD.equals(sootClass.getName())) {
			return false;
		}
		Set<SootMethod> sootMethods = new HashSet<SootMethod>(sootClass.getMethods());
		for (SootMethod sootMethod : sootMethods) {
			if (isPreDestroyAnnotationMethod(sootMethod) || isDestroyMethod(sootMethod) || isCloseMethod(sootMethod)) {
				destructibleClasses.put(originClass, sootMethod);
				if(Scene.v().getApplicationClasses().contains(originClass) && !originClass.isAbstract() && isEmptyMethod(sootMethod)) {
					Log.e("#####################");
					Log.e(originClass.getName());
					Log.e(sootMethod);
					emptyDestructibleClasses.put(originClass.getName() , sootMethod);
				}
				return true;
			}
		}
		if (sootClass.hasSuperclass()) {
			if (isDestructibleClass(originClass, sootClass.getSuperclass())) {
				return true;
			}
		}
		if (sootClass.getInterfaces() != null) {
			for (SootClass interfaceClass : sootClass.getInterfaces()) {
				if (isDestructibleClass(originClass, interfaceClass)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean isPreDestroyAnnotationMethod(SootMethod sootMethod) {
		List<AnnotationTag> annotationTags = getAnnotationtags(sootMethod);
		for (AnnotationTag annotationTag : annotationTags) {
			//Ljavax/annotation/PreDestroy;
			//Ljakarta/annotation/PreDestroy;
			//xxx/PostConstruct;
			if (annotationTag.getType().endsWith("PreDestroy") || annotationTag.getType().endsWith("PostConstruct")) {
				annotationClasses.add(sootMethod.getDeclaringClass().getName());
				return true;
			}
		}
		return false;
	}
	
	private static boolean isDestroyMethod(SootMethod sootMethod) {
		if(sootMethod.getName().toLowerCase().endsWith("destroy")) {
			destroyClasses.add(sootMethod.getDeclaringClass().getName());
			return true;
		}
		return false;
	}
	
	private static boolean isCloseMethod(SootMethod sootMethod) {
		if(sootMethod.getName().toLowerCase().contains("close")) {
			closeClasses.add(sootMethod.getDeclaringClass().getName());
			return true;
		}
		return false;
	}

	private static List<AnnotationTag> getAnnotationtags(AbstractHost host) {
		List<AnnotationTag> tags = new ArrayList<>();
		for (Tag tag : host.getTags()) {
			if (tag instanceof VisibilityAnnotationTag) {
				VisibilityAnnotationTag visibilityAnnotationTag = (VisibilityAnnotationTag) tag;
				tags.addAll(visibilityAnnotationTag.getAnnotations());
			}
		}
		return tags;
	}
	
	public static final String Android_Platforms = "/HostServer/home/cuibaoquan/android-platforms";

	public static void main(String[] args) {
		String bench = args[0];
//		String path = "G:\\code\\SpringFramework";
//		String path = "/HostServer/home/hongwj/scripts/fdroid/2021/" + bench;
		
 
		String path = "/HostServer/home/cuibaoquan/AsyncCompoment/ThreadBenchmark/" + bench;
		
		//String path = "C:\\Users\\C\\eclipseworkspace\\code\\EmptyMethod\\bin" + bench;

		List<String> jars = new ArrayList<String>();
		jars.add(path);
		Log.i(path);
		if (path.toLowerCase().endsWith(".jar")) {
			jars.add(path);
		} else {
			jars.addAll(getJars(path));
		}
		Log.e(jars.size());
		G.reset();
//		Options.v().set_android_jars(Android_Platforms);
		Options.v().set_process_dir(jars);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_process_multiple_dex(true);
		Options.v().set_no_writeout_body_releasing(true); // must be set to true if we want to access method bodies
															// after writing output to jimple
		Options.v().set_output_format(Options.output_format_none);
//		Options.v().set_src_prec(Options.src_prec_class);
		Options.v().set_src_prec(Options.src_prec_c);
		Options.v().allow_phantom_refs();
		Options.v().set_whole_program(true);
//		Options.v().set_exclude(getExcludeList());

		Pack p1 = PackManager.v().getPack("jtp");
		String phaseName = "jtp.bt";

		Transform t1 = new Transform(phaseName, new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phase, Map<String, String> options) {
				b.getMethod().setActiveBody(b);
			}
		});

		p1.add(t1);

		soot.Main.v().autoSetOptions();

		try {
			Scene.v().loadNecessaryClasses();
		} catch (Exception e) {
			e.printStackTrace();
		}

		collect();
		String content = "########"+bench+"########\r\n";
		content += "##########annotationClasses size =  " + DestructibleIdentify.annotationClasses.size() + "\r\n";
		content += "##########closeClasses size =  " + DestructibleIdentify.closeClasses.size() + "\r\n";
		content += "##########destroyClasses size =  " + DestructibleIdentify.destroyClasses.size() + "\r\n";
		content += "##########destructibleClasses size =  " + DestructibleIdentify.destructibleClasses.size() + "\r\n";
		content += "##########emptyDestructibleClasses size =  " + DestructibleIdentify.emptyDestructibleClasses.size() + "\r\n";
//		for (SootClass string : DestructibleIdentify.destructibleClasses.keySet()) {
//			content += string + "\r\n";
//		}
		content += "##########emptyDestructibleClasses##########\r\n";
		for (String string : DestructibleIdentify.emptyDestructibleClasses.keySet()) {
			content += string +  DestructibleIdentify.emptyDestructibleClasses.get(string).getSignature() + "\r\n";
		}
		content += "\r\n\r\n";
		record(content, "destructible_0825_1052.txt");
//
//		PackManager.v().runPacks();
	}
	
	static void record(String content, String filePath) {
		FileWriter errorWriter = null;
		try {
			errorWriter = new FileWriter(filePath, true);
			errorWriter.write(content);
		} catch (Exception e) {
			e.printStackTrace();
//			saveException(e);
		} finally {
			try {
				errorWriter.close();
			} catch (Exception | Error e) {
				e.printStackTrace();
			}
		}
	}

	public static Set<String> getJars(String dic) {
		File file = new File(dic);
		Set<String> jarList = new HashSet<String>();
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			if (fileList != null) {
				for (File subFile : fileList) {
					String string = subFile.getAbsolutePath();
					if (subFile.isDirectory()) {
						jarList.addAll(getJars(string));
					} else {
						if (string.toLowerCase().endsWith(".jar")) {
							jarList.add(string);
						}
					}

				}
			}
		}
		return jarList;
	}
	
	 static List<String> getExcludeList() {
		ArrayList<String> excludeList = new ArrayList<String>();
//		excludeList.add("android.*");
//		excludeList.add("androidx.*");
//		excludeList.add("org.*");
//		excludeList.add("soot.*");
		excludeList.add("java.*");
		excludeList.add("sun.*");
		excludeList.add("javax.*");
		excludeList.add("com.sun.*");
//		excludeList.add("com.ibm.*");
		excludeList.add("org.xml.*");
		excludeList.add("org.w3c.*");
//		excludeList.add("apple.awt.*");
//		excludeList.add("com.apple.*");
		return excludeList;
	}
}
