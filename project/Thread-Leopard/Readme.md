# Leopard

A lightweight flow-, context-sensitive and inter-procedural static analysis tool for misuse detection of _java.lang.Thread_.

we can use it through following command:

    java -cp Leopard.jar ac.LeopardMain [options]

The command line options of Leopard are shown as following:
    
```
    args[0] input path:
	 * 		apk: {path}/name.apk   
	 * 		jar: 	{path}/name.jar  or 	{path}/   (all jars in the dictionary will be detected) 
	args[1]  output path
	args[2]  refinement
	 * 		withRefinement:     1
	 * 		withoutRefinement:  0
	args[3]  android-platforms path (if args[0] is for apk)
	 *      {path}/android-platforms
	
```

For Android apk withRefinement:
```
    java -cp Leopard.jar ac.LeopardMain  /{path}/apkName.apk ./output/  1  ./android-platform
```

For a single jar file withoutRefinement:
```
    java -cp Leopard.jar ac.LeopardMain  /{path}/name.jar ./output/  0
```

For a dictionary containing jar files withRefinement (Simultaneous analysis of multiple apks is not supported):
```
    java -cp Leopard.jar ac.LeopardMain  /{path}/apache-cxf-3.5.2/ ./output/  1
```