package destructible;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.options.Options;
import soot.tagkit.AbstractHost;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.constant.Signature;
import ac.record.ThreadErrorRecord;
import ac.util.Log;

public class DestructibleIdentify {

	public final static Set<String> destructibleClasses = new HashSet<>();
	public final static Set<String> destroyClasses = new HashSet<>();
	public final static Set<String> closeClasses = new HashSet<>();
	public final static Set<String> cleanClasses = new HashSet<>();
	public final static Set<String> annotationClasses = new HashSet<>();

	public static void collect() {
		Set<SootClass> classes = new HashSet<SootClass>();
		classes.addAll(Scene.v().getClasses());
		classes.addAll(Scene.v().getApplicationClasses());
		for (SootClass sootClass : classes) {
			if (isDestructibleClass(sootClass)) {
				destructibleClasses.add(sootClass.getName());
			} 
		}
	}
	
	public static boolean isDestructibleClassPreDestroyAnnotation(SootClass sootClass) {
		if (destructibleClasses.contains(sootClass.getName())) {
			return true;
		}
		if(Signature.CLASS_THREAD.equals(sootClass.getName())) {
			return false;
		}
		Set<SootMethod> sootMethods = new HashSet<SootMethod>(sootClass.getMethods());
		for (SootMethod sootMethod : sootMethods) {
			List<AnnotationTag> annotationTags = getAnnotationtags(sootMethod);
			for (AnnotationTag annotationTag : annotationTags) {
				if ("Ljavax/annotation/PreDestroy;".equals(annotationTag.getType())
						|| "Ljakarta/annotation/PreDestroy;".equals(annotationTag.getType())) {
					annotationClasses.add(sootClass.getName());
					return true;
				}
			}

		}
		if (sootClass.hasSuperclass()) {
			if (isDestructibleClass(sootClass.getSuperclass())) {
				return true;
			}
		}
		if (sootClass.getInterfaces() != null) {
			for (SootClass interfaceClass : sootClass.getInterfaces()) {
				if (isDestructibleClass(interfaceClass)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isDestructibleClass(SootClass sootClass) {
		if (destructibleClasses.contains(sootClass.getName())) {
			return true;
		}
		if(Signature.CLASS_THREAD.equals(sootClass.getName())) {
			return false;
		}
		Set<SootMethod> sootMethods = new HashSet<SootMethod>(sootClass.getMethods());
		for (SootMethod sootMethod : sootMethods) {
			List<AnnotationTag> annotationTags = getAnnotationtags(sootMethod);
			for (AnnotationTag annotationTag : annotationTags) {
				if ("Ljavax/annotation/PreDestroy;".equals(annotationTag.getType())
						|| "Ljakarta/annotation/PreDestroy;".equals(annotationTag.getType())) {
					annotationClasses.add(sootClass.getName());
					return true;
				}
				if(annotationTag.getType().contains("PostConstruct")) {
					annotationClasses.add(sootClass.getName());
					return true;
				}

			}
			if (sootMethod.getName().toLowerCase().equals("destroy")
					|| sootMethod.getName().toLowerCase().equals("preDestroy")
					|| sootMethod.getName().toLowerCase().equals("onDestroy")) {
				destroyClasses.add(sootClass.getName());
				return true;
			}
			else if (sootMethod.getName().toLowerCase().contains("close")) {
				closeClasses.add(sootClass.getName());
				return true;
			}

		}
		if (sootClass.hasSuperclass()) {
			if (isDestructibleClass(sootClass.getSuperclass())) {
				return true;
			}
		}
		if (sootClass.getInterfaces() != null) {
			for (SootClass interfaceClass : sootClass.getInterfaces()) {
				if (isDestructibleClass(interfaceClass)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<AnnotationTag> getAnnotationtags(AbstractHost host) {
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
//		String content = DestructibleIdentify.annotationClasses.size() + "\r\n";
		String content = "annotationClasses size =  " + DestructibleIdentify.annotationClasses.size() + "\r\n";
		for (String string : DestructibleIdentify.annotationClasses) {
			content += string + "\r\n";
		}
//		LeopardMain.apk_name = "destructibleRecord.apk";
//		ThreadErrorRecord.record(content, "destructibleClass_apk.txt");
		ThreadErrorRecord.record(content, "destructible_annotation.txt");
//
//		PackManager.v().runPacks();
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
