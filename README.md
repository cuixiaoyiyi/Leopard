# Leopard

(This repository will be made public if the paper is accepted)

A lightweight tool to detect thread related misuses causing resource leaks in Java.

In the Java platform, it is common to use the fundamental asynchronous thread (java.lang.Thread). However, due to garbage collection and thread interruption mechanism, it can be easily misused. The recycle of an object will be blocked if an alive thread holds a strong reference to it. In addition, the careless implementation of asynchronous thread may cause that there is not responding to the interrupt mechanism. This may result the unexpected thread related behavior and resource leak/waste.

we implement a lightweight tool named Leopard to detect them statically, which is a very fast one with the demand-driven slicing analysis. It reduces false negatives caused by the heavyweight time-consuming path sensitive analysis proposed by existing work.

## 0. FeedBack of Developers (Confirmed: 64; Fixed: 19)
|NO.|App|Fork|Star|#Download on GooglePaly |#Misuse (*Fixed)|Confirmed Issue Id|
|-|-|-|-|-|-|-|
|01|VocableTrainer|10|27|-|1|[93](https://github.com/0xpr03/VocableTrainer-Android/issues/93)|
|02|toposuite|2|12|[5,000+](https://play.google.com/store/apps/details?id=ch.hgdev.toposuite)|4|[3](https://github.com/hgdev-ch/toposuite-android/issues/3)|
|03|APK-Explorer-Editorcite | 53 | 278 |[100+](https://play.google.com/store/apps/details?id=com.apk.explorer)  |1*|[29](https://github.com/apk-editor/APK-Explorer-Editor/issues/29) |
|04|LRC-Editorcite | 9 | 43 |[100,000+](https://play.google.com/store/apps/details?id=com.cg.lrceditor)| 3 |[35](https://github.com/Spikatrix/LRC-Editor/issues/35) |
|05|Nextcloudcite | 1.5K | 3.2K |[100,000+](https://play.google.com/store/apps/details?id=com.nextcloud.client)| 7 |[10691](https://github.com/nextcloud/android/issues/10691) |
|06|TRIfAcite | 52 | 220 | -| 14 |[382](https://github.com/twireapp/Twire/issues/382) |
|07|AppManagercite | 174 | 2.3K | -| 1 |[854](https://github.com/MuntashirAkon/AppManager/issues/854) |
|08|Siteswap Generatorcite | 3 | 13 | [1,000+](https://play.google.com/store/apps/details?id=namlit.siteswapgenerator) | 9 |[55](https://github.com/namlit/siteswap_generator/issues/55) |
|09|TC Slimcite | 66 | 1.1K | [10,000+](https://play.google.com/store/apps/details?id=net.kollnig.missioncontrol.play) | 2 |[336](https://github.com/TrackerControl/tracker-control-android/issues/336) |
|10|blabber.imcite | 16 | 41 | - | 6* |[674](https://codeberg.org/kriztan/blabber.im/issues/674) |
|11|OSMDashboardcite | 8 | 52 | [500+](https://play.google.com/store/apps/details?id=de.storchp.opentracks.osmplugin) | 1* |[169](https://github.com/OpenTracksApp/OSMDashboard/issues/169) |
|12|Ghost Commander | - | - | [1,000,000+](https://play.google.com/store/apps/details?id=com.ghostsq.commander) | 1* |[93](https://sourceforge.net/p/ghostcommander/bugs/93/) |
|13|Offline Puzzle Solver | - | 1 | - | 1* |[1](https://gitlab.com/20kdc/offline-puzzle-solver/issues/1) |
|14|FitoTrack | 49 | 161 | [5,000+](https://play.google.com/store/apps/details?id=de.tadris.fitness) | 3 | [400](https://codeberg.org/jannis/FitoTrack/issues/400) |
|15|Conversations | 1.3K | 4.2K | [100,000+](https://play.google.com/store/apps/details?id=eu.siacs.conversations) | 2* | [4366](https://github.com/iNPUTmice/Conversations/issues/4366)|
|16|monocles chat | 7 | 10 | - | 6* | [44](https://codeberg.org/Arne/monocles_chat/issues/44) |
|17|ccgt| 4 | 11 | - | 1 | [7](https://github.com/pterodactylus42/ccgt/issues/7)  |
|18|Notes| 121 | 769 | 10K+ | 1* | [1574](https://github.com/nextcloud/notes-android/issues/1574)  |
|-|Total |-|-|-|64(Fixed 19)|- 



## 1. Tools
### 1.1 [Leopard](./project/Leopard)
### 1.2 [eAsyncChecker](./project/Thread-eAsyncChecker)

[android-platforms](./project/android-platforms)(Dependencies required for soot to resolve Android)


## 2. Benchmark

###  2.1 Java Programs [Go](./Benchmark)  
|project|version|link|
|-|-|-|
|apache-cxf|3.5.2|[Go](./Benchmark/JavaPrograms/apache-cxf-3.5.2)|
|apache-tomcat|10.0.20|[Go](./Benchmark/JavaPrograms/apache-tomcat-10.0.20)|
|micronaut-cli|3.4.2|[Go](./Benchmark/JavaPrograms/micronaut-cli-3.4.2)|
|hivemq-ce|2021.3|[Go](./Benchmark/JavaPrograms/hivemq-ce-2021.3)|
|jetty-distribution|9.4.46.v20220331|[Go](./Benchmark/JavaPrograms/jetty-distribution-9.4.46.v20220331)|
|Junit5|-|[Go](./Benchmark/JavaPrograms/Junit5)|
|quarkus-cli|2.8.0.Final|[Go](./Benchmark/JavaPrograms/quarkus-cli-2.8.0.Final)|
|SpringFramework|5.3.18|[Go](./Benchmark/JavaPrograms/5.3.18)|
|wildfly|26.1.0.Beta1|[Go](./Benchmark/JavaPrograms/wildfly-26.1.0.Beta1)|

### 2.2 Android Apps [GO](./Benchmark/Apks)
download date : 2021-09-21 from F-Droid 
