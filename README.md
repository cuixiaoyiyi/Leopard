# Leopard

(This repository will be made public if the paper is accepted)


A lightweight tool to detect thread related misuses causing resource leaks in Java.

In the Java platform, it is common to use the fundamental asynchronous thread (java.lang.Thread). However, due to garbage collection and thread interruption mechanism, it can be easily misused. The recycle of an object will be blocked if an alive thread holds a strong reference to it. In addition, the careless implementation of asynchronous thread may cause that there is not responding to the interrupt mechanism. This may result the unexpected thread related behavior and resource leak/waste.

we implement a lightweight tool named Leopard to detect them statically, which is a very fast one with the demand-driven slicing analysis. It reduces false negatives caused by the heavyweight time-consuming path sensitive analysis proposed by existing work.

## 0. FeedBack of Developers (Confirmed: 63; Fixed: 18)
|NO.|App|Fork|Star|#Download on GooglePaly |#Misuse (*Fixed)|Confirmed Issue Id|
|-|-|-|-|-|-|-|
|01|VocableTrainer|10|27|-|1|[93](https://github.com/0xpr03/VocableTrainer-Android/issues/93)|
|02|toposuite|2|12|[5,000+](https://play.google.com/store/apps/details?id=ch.hgdev.toposuite)|4|[3](https://github.com/hgdev-ch/toposuite-android/issues/3)|
|03|APK-Explorer-Editorcite | 47 | 254 |[100+](https://play.google.com/store/apps/details?id=com.apk.explorer)  |1*|[29](https://github.com/apk-editor/APK-Explorer-Editor/issues/29) |
|04|LRC-Editorcite | 9 | 42 |[100,000+](https://play.google.com/store/apps/details?id=com.cg.lrceditor)| 3 |[35](https://github.com/Spikatrix/LRC-Editor/issues/35) |
|05|Nextcloudcite | 1.4K | 3K |[100,000+](https://play.google.com/store/apps/details?id=com.nextcloud.client)| 7 |[10691](https://github.com/nextcloud/android/issues/10691) |
|06|TRIfAcite | 51 | 203 | -| 14 |[382](https://github.com/twireapp/Twire/issues/382) |
|07|AppManagercite | 138 | 1.8K | -| 1 |[854](https://github.com/MuntashirAkon/AppManager/issues/854) |
|08|Siteswap Generatorcite | 3 | 13 | [1,000+](https://play.google.com/store/apps/details?id=namlit.siteswapgenerator) | 9 |[55](https://github.com/namlit/siteswap_generator/issues/55) |
|09|TC Slimcite | 58 | 978 | [10,000+](https://play.google.com/store/apps/details?id=net.kollnig.missioncontrol.play) | 2 |[336](https://github.com/TrackerControl/tracker-control-android/issues/336) |
|10|blabber.imcite | 58 | 978 | - | 6* |[674](https://codeberg.org/kriztan/blabber.im/issues/674) |
|11|OSMDashboardcite | 7 | 45 | [500+](https://play.google.com/store/apps/details?id=de.storchp.opentracks.osmplugin) | 1* |[169](https://github.com/OpenTracksApp/OSMDashboard/issues/169) |
|12|Ghost Commander | - | - | [1,000,000+](https://play.google.com/store/apps/details?id=com.ghostsq.commander) | 1* |[93](https://sourceforge.net/projects/ghostcommander/issues/93) |
|13|Offline Puzzle Solver | - | 1 | - | 1* |[1](https://gitlab.com/20kdc/offline-puzzle-solver/issues/1) |
|14|FitoTrack | 147 | 45 | [5,000+](https://play.google.com/store/apps/details?id=de.tadris.fitness) | 3 | [400](https://codeberg.org/jannis/FitoTrack/issues/400) |
|15|Conversations | 1.2K | 4.1K | [100,000+](https://play.google.com/store/apps/details?id=eu.siacs.conversations) | 2* | [4366](https://github.com/iNPUTmice/Conversations/issues/4366)|
|16|monocles chat | 6 | 10 | - | 6* | [44](https://codeberg.org/Arne/monocles_chat/issues/44) |
|17|ccgt| 3 | 8 | - | 1 | [7](https://github.com/pterodactylus42/ccgt\issues\7)  |
|-|Total |-|-|-|63(Fixed 18)|- 



## 1. Tools
### 1.1 [Leopard](./project/Thread-Leopard)
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

## 3. Experimental Result

### 3.1 Java Programs (without FP refinement)
|Program|HTR|INR|NTT|
|-|-|-|-|
|apache-cxf-3.5.2|7|19|7|
|apache-tomcat-10.0.20|1|2|1|
|hivemq-ce-2021.3|1|2|1|
|jetty-distribution-9.4.46.v20220331|3|1|3|
|Junit5|0|1|0|
|micronaut-cli-3.4.2|5|2|5|
|quarkus-cli-2.8.0.Final|3|5|3|
|SpringFramework5.3.18|3|6|3|
|wildfly-26.1.0.Beta1|10|19|10|
|sum|33|57|33|

### 3.2 Java Programs (with FP refinement)
|Program|HTR|INR|NTT|Time(s)|
|-|-|-|-|-|
|apache-cxf-3.5.2|7|19|7|695|
|apache-tomcat-10.0.20|1|2|1|73|
|hivemq-ce-2021.3|1|2|1|556|
|jetty-distribution-9.4.46.v20220331|3|1|3|71|
|Junit5|0|1|0|15|
|micronaut-cli-3.4.2|5|3|5|285|
|quarkus-cli-2.8.0.Final|3|5|3|203|
|SpringFramework5.3.18|2|6|2|256|
|wildfly-26.1.0.Beta1|8|9|8|1035|
|sum|30|48|30|3193(319 on average)|


### Android App

#### 3.3 Leopard (without FP refinement)
|App|HTR|INR|NTT|
|-|-|-|-|
|net.sourceforge.opencamera|1|2|1|
|com.github.axet.torrentclient|0|1|0|
|se.lublin.mumla|1|0|1|
|com.igisw.openmoneybox|1|0|1|
|com.freerdp.afreerdp|1|0|1|
|org.snikket.android|2|2|2|
|lu.fisch.canze|7|1|7|
|it.niedermann.owncloud.notes|2|0|2|
|espero.jiofibatterynotifier|1|0|1|
|xyz.myachin.saveto|2|0|2|
|com.fr3ts0n.ecu.gui.androbd|0|1|0|
|posidon.launcher|0|1|0|
|fr.gouv.etalab.mastodon|9|2|9|
|is.xyz.omw|0|0|0|
|com.farmerbb.taskbar|2|0|2|
|ru.yanus171.feedexfork|1|20|1|
|net.i2p.android.router|0|3|0|
|net.osmand.plus|0|0|0|
|menion.android.whereyougo|1|0|1|
|de.corona.tracing|0|0|0|
|t20kdc.offlinepuzzlesolver|0|1|0|
|player.efis.cfd|0|0|0|
|player.efis.mfd|0|0|0|
|com.gh4a|0|0|0|
|io.github.chronosx88.yggdrasil|0|1|0|
|io.github.installalogs|1|0|1|
|de.drhoffmannsoftware.calcvac|0|0|0|
|de.eidottermihi.raspicheck|0|0|0|
|com.haringeymobile.ukweather|2|0|2|
|io.github.subhamtyagi.ocr|0|1|0|
|mobi.omegacentauri.SendReduced|0|0|0|
|io.github.muntashirakon.AppManager|5|0|5|
|site.leos.apps.lespas|1|0|1|
|vocabletrainer.heinecke.aron.vocabletrainer|1|0|1|
|com.dar.nclientv2|1|3|1|
|com.iatfei.streakalarm|0|0|0|
|com.github.cvzi.darkmodewallpaper|0|2|0|
|namlit.siteswapgenerator|9|0|9|
|xyz.myachin.downloader|1|0|1|
|net.sourceforge.x11basic|0|0|0|
|com.kylecorry.trail_sense|0|0|0|
|de.storchp.opentracks.osmplugin|1|0|1|
|sushi.hardcore.droidfs|1|0|1|
|com.mendhak.gpslogger|0|0|0|
|com.cg.lrceditor|11|0|11|
|com.smartpack.kernelmanager|0|3|0|
|com.dozingcatsoftware.bouncy|0|0|0|
|app.fedilab.tubelab|18|0|18|
|theredspy15.ltecleanerfoss|2|0|2|
|at.jclehner.rxdroid|0|5|0|
|app.fedilab.mobilizon|2|0|2|
|com.perflyst.twire|0|0|0|
|com.limbo.emu.main|19|0|19|
|de.luhmer.owncloudnewsreader|0|1|0|
|com.smartpack.smartflasher|0|0|0|
|com.poupa.attestationdeplacement|2|0|2|
|kiwi.root.an2linuxclient|0|0|0|
|dev.msfjarvis.aps|0|0|0|
|ch.hgdev.toposuite|4|0|4|
|ltd.evilcorp.atox|0|0|0|
|net.pfiers.osmfocus|0|0|0|
|com.machiav3lli.backup|2|0|2|
|com.gimranov.zandy.app|1|0|1|
|eu.siacs.conversations|2|2|2|
|io.github.folderlogs|1|0|1|
|de.tadris.fitness|3|1|3|
|de.syss.MifareClassicTool|2|0|2|
|com.calcitem.sanmill|0|0|0|
|io.github.saveastxt|1|0|1|
|de.nulide.findmydevice|0|2|0|
|com.ichi2.anki|0|1|0|
|com.sapuseven.untis|0|1|0|
|com.tunerly|0|0|0|
|de.freehamburger|0|3|0|
|com.limelight|1|1|1|
|net.justdave.nwsweatheralertswidget|1|0|1|
|com.github.gschwind.fiddle_assistant|0|0|0|
|de.fff.ccgt|1|0|1|
|godau.fynn.usagedirect|2|0|2|
|de.rwth_aachen.phyphox|0|0|0|
|com.mikifus.padland|0|0|0|
|net.ktnx.mobileledger|0|2|0|
|com.github.axet.audiorecorder|0|3|0|
|com.securefilemanager.app|0|0|0|
|de.blau.android|0|1|0|
|la.daube.photochiotte|0|0|0|
|me.blog.korn123.easydiary|1|0|1|
|top.donmor.tiddloid|0|0|0|
|com.spisoft.quicknote|3|1|3|
|com.smartpack.packagemanager|0|2|0|
|im.status.ethereum|0|0|0|
|de.sudoq|0|1|0|
|net.kollnig.missioncontrol.fdroid|2|0|2|
|com.swordfish.lemuroid|0|0|0|
|com.apk.editor|0|1|0|
|com.ominous.quickweather|0|0|0|
|com.nextcloud.android.beta|6|1|6|
|io.github.randomfilemaker|1|0|1|
|com.brentpanther.bitcoinwidget|0|0|0|
|com.ghostsq.commander|0|3|0|
|com.nextcloud.client|6|1|6|
|godau.fynn.usagedirect.system|1|0|1|
|dev.corruptedark.openchaoschess|1|2|1|
|com.cweb.messenger|2|2|2|
|apps.amine.bou.readerforselfoss|0|1|0|
|com.wireguard.android|0|0|0|
|com.smartpack.scriptmanager|0|2|0|
|com.jovial.jrpn|1|1|1|
|nodomain.freeyourgadget.gadgetbridge|1|0|1|
|com.oF2pks.classyshark3xodus|1|2|1|
|com.dosse.airpods|0|0|0|
|com.owncloud.android|0|0|0|
|it.niedermann.nextcloud.deck|0|0|0|
|me.tripsit.tripmobile|0|0|0|
|fr.gouv.android.stopcovid|0|0|0|
|net.nhiroki.bluelineconsole|4|0|2|
|de.storchp.opentracks.osmplugin.offline|1|1|1|
|com.ubergeek42.WeechatAndroid|0|0|0|
|com.smartpack.kernelprofiler|0|1|0|
|de.monocles.chat|5|2|5|
|app.fedilab.lite|9|2|9|
|com.asdoi.gymwen|7|0|7|
|com.simplemobiletools.gallery.pro|0|0|0|
|im.quicksy.client|2|2|2|
|com.appmindlab.nano|0|4|0|
|com.dosse.bwentrain.androidPlayer|0|3|0|
|com.alienpants.leafpicrevived|1|0|1|
|com.dosse.dozeoff|0|0|0|
|su.sadrobot.yashlang|17|0|17|
|io.github.datastopwatch|1|0|1|
|de.reimardoeffinger.quickdic|1|0|1|
|com.osfans.trime|0|0|0|
|eu.veldsoft.ithaka.board.game|2|0|2|
|com.machiav3lli.derdiedas|2|0|2|
|com.tnibler.cryptocam|0|0|0|
|com.github.axet.bookreader|2|3|2|
|net.bible.android.activity|0|0|0|
|de.pixart.messenger|6|2|6|
|de.dennisguse.opentracks|2|0|2|
|security.pEp|0|2|0|
|dnsfilter.android|2|0|2|
|de.rochefort.childmonitor|2|0|2|
|eu.sum7.conversations|2|2|2|
|app.fedilab.fedilabtube|18|0|18|
|io.github.silinote|1|0|1|
|com.shatteredpixel.shatteredpixeldungeon|0|0|0|
|com.zoffcc.applications.trifa|0|22|0|
|sum|242|133|240|

#### 3.4 Leopard (with FP refinement)
|App|HTR|INR|NTT|Time(ms)|
|-|-|-|-|-|
|net.sourceforge.opencamera|1|2|1|11864|
|com.github.axet.torrentclient|0|1|0|10925|
|se.lublin.mumla|1|0|1|20002|
|com.igisw.openmoneybox|1|0|1|11963|
|com.freerdp.afreerdp|1|0|1|5750|
|org.snikket.android|2|2|2|21807|
|lu.fisch.canze|16|1|16|6718|
|it.niedermann.owncloud.notes|2|0|2|18716|
|espero.jiofibatterynotifier|1|0|1|7957|
|xyz.myachin.saveto|2|0|2|7791|
|com.fr3ts0n.ecu.gui.androbd|0|1|0|10774|
|posidon.launcher|0|1|0|16625|
|fr.gouv.etalab.mastodon|8|2|8|45888|
|is.xyz.omw|0|0|0|12976|
|com.farmerbb.taskbar|3|0|3|7342|
|ru.yanus171.feedexfork|1|20|1|20277|
|net.i2p.android.router|0|3|0|17347|
|net.osmand.plus|2|2|2|173804|
|menion.android.whereyougo|1|0|1|11024|
|de.corona.tracing|0|0|0|21392|
|t20kdc.offlinepuzzlesolver|0|1|0|3347|
|player.efis.cfd|0|0|0|5550|
|player.efis.mfd|0|0|0|5067|
|com.gh4a|0|0|0|24105|
|io.github.chronosx88.yggdrasil|0|1|0|11112|
|io.github.installalogs|1|0|1|12215|
|de.drhoffmannsoftware.calcvac|0|0|0|7336|
|de.eidottermihi.raspicheck|0|0|0|18054|
|com.haringeymobile.ukweather|2|0|2|6048|
|io.github.subhamtyagi.ocr|0|1|0|12890|
|mobi.omegacentauri.SendReduced|0|0|0|4333|
|io.github.muntashirakon.AppManager|2|0|2|22100|
|site.leos.apps.lespas|1|0|1|23012|
|vocabletrainer.heinecke.aron.vocabletrainer|1|0|1|6881|
|com.dar.nclientv2|0|5|0|8935|
|com.iatfei.streakalarm|0|0|0|16056|
|com.github.cvzi.darkmodewallpaper|0|2|0|11697|
|namlit.siteswapgenerator|9|0|9|6538|
|xyz.myachin.downloader|1|0|1|10618|
|net.sourceforge.x11basic|0|0|0|5443|
|com.kylecorry.trail_sense|0|0|0|11237|
|de.storchp.opentracks.osmplugin|1|1|1|10555|
|sushi.hardcore.droidfs|1|0|1|12097|
|com.mendhak.gpslogger|0|0|0|8993|
|com.cg.lrceditor|11|0|11|9200|
|com.smartpack.kernelmanager|0|3|0|9836|
|com.dozingcatsoftware.bouncy|0|0|0|9530|
|app.fedilab.tubelab|18|0|18|23981|
|theredspy15.ltecleanerfoss|2|0|2|6186|
|at.jclehner.rxdroid|0|5|0|11983|
|app.fedilab.mobilizon|2|0|2|11801|
|com.perflyst.twire|1|0|1|14401|
|com.limbo.emu.main|8|0|8|7018|
|de.luhmer.owncloudnewsreader|0|1|0|12557|
|com.smartpack.smartflasher|0|1|0|8982|
|com.poupa.attestationdeplacement|2|0|2|14550|
|kiwi.root.an2linuxclient|0|0|0|16260|
|dev.msfjarvis.aps|0|0|0|21497|
|ch.hgdev.toposuite|4|0|4|6094|
|ltd.evilcorp.atox|0|0|0|9459|
|net.pfiers.osmfocus|0|0|0|20069|
|com.machiav3lli.backup|1|0|1|11552|
|com.gimranov.zandy.app|1|0|1|7852|
|eu.siacs.conversations|2|2|2|23681|
|io.github.folderlogs|1|0|1|9105|
|de.tadris.fitness|3|0|3|18281|
|de.syss.MifareClassicTool|2|0|2|5920|
|com.calcitem.sanmill|0|0|0|5569|
|io.github.saveastxt|1|0|1|10148|
|de.nulide.findmydevice|0|2|0|18753|
|com.ichi2.anki|0|1|0|29641|
|com.sapuseven.untis|0|1|0|16740|
|com.tunerly|0|0|0|18435|
|de.freehamburger|0|3|0|15278|
|com.limelight|1|1|1|9176|
|net.justdave.nwsweatheralertswidget|1|0|1|6532|
|com.github.gschwind.fiddle_assistant|0|0|0|5923|
|de.fff.ccgt|1|0|1|7302|
|godau.fynn.usagedirect|2|0|2|7594|
|de.rwth_aachen.phyphox|0|0|0|10995|
|com.mikifus.padland|0|0|0|8156|
|net.ktnx.mobileledger|0|2|0|11902|
|com.github.axet.audiorecorder|0|3|0|10763|
|com.securefilemanager.app|0|0|0|12868|
|de.blau.android|0|1|0|24890|
|la.daube.photochiotte|0|0|0|12736|
|me.blog.korn123.easydiary|1|0|1|22472|
|top.donmor.tiddloid|0|0|0|8048|
|com.spisoft.quicknote|3|1|3|11675|
|com.smartpack.packagemanager|0|1|0|6462|
|im.status.ethereum|0|0|0|24745|
|de.sudoq|0|1|0|7698|
|net.kollnig.missioncontrol.fdroid|2|0|2|17448|
|com.swordfish.lemuroid|0|0|0|14951|
|com.apk.editor|0|1|0|8021|
|com.ominous.quickweather|0|0|0|11628|
|com.nextcloud.android.beta|6|1|6|47174|
|io.github.randomfilemaker|1|0|1|13311|
|com.brentpanther.bitcoinwidget|0|0|0|6409|
|com.ghostsq.commander|0|3|0|5669|
|com.nextcloud.client|7|1|7|50066|
|godau.fynn.usagedirect.system|1|0|1|10037|
|dev.corruptedark.openchaoschess|1|3|1|8771|
|com.cweb.messenger|2|2|2|19707|
|apps.amine.bou.readerforselfoss|0|1|0|15715|
|com.wireguard.android|0|0|0|7466|
|com.smartpack.scriptmanager|0|1|0|5886|
|com.jovial.jrpn|1|1|1|7809|
|nodomain.freeyourgadget.gadgetbridge|1|0|1|15810|
|com.oF2pks.classyshark3xodus|1|2|1|10278|
|com.dosse.airpods|0|0|0|10754|
|com.owncloud.android|0|0|0|29158|
|it.niedermann.nextcloud.deck|0|0|0|28365|
|me.tripsit.tripmobile|0|0|0|8149|
|fr.gouv.android.stopcovid|0|0|0|17762|
|net.nhiroki.bluelineconsole|4|0|2|8628|
|de.storchp.opentracks.osmplugin.offline|1|1|1|11919|
|com.ubergeek42.WeechatAndroid|0|0|0|10669|
|com.smartpack.kernelprofiler|0|1|0|11124|
|de.monocles.chat|6|2|6|32034|
|app.fedilab.lite|9|2|9|35862|
|com.asdoi.gymwen|6|0|6|16144|
|com.simplemobiletools.gallery.pro|0|0|0|17799|
|im.quicksy.client|2|2|2|22659|
|com.appmindlab.nano|0|4|0|11220|
|com.dosse.bwentrain.androidPlayer|0|3|0|7467|
|com.alienpants.leafpicrevived|1|0|1|9805|
|com.dosse.dozeoff|0|0|0|4280|
|su.sadrobot.yashlang|17|0|17|18904|
|io.github.datastopwatch|1|0|1|11207|
|de.reimardoeffinger.quickdic|1|0|1|8910|
|com.osfans.trime|0|0|0|4658|
|eu.veldsoft.ithaka.board.game|2|0|2|10661|
|com.machiav3lli.derdiedas|2|0|2|11471|
|com.tnibler.cryptocam|0|0|0|14613|
|com.github.axet.bookreader|2|3|2|20258|
|net.bible.android.activity|0|0|0|11420|
|de.pixart.messenger|5|2|5|42156|
|de.dennisguse.opentracks|2|0|2|13858|
|security.pEp|0|2|0|37016|
|dnsfilter.android|2|0|2|5542|
|de.rochefort.childmonitor|2|0|2|6072|
|eu.sum7.conversations|2|2|2|21338|
|app.fedilab.fedilabtube|18|0|18|32620|
|io.github.silinote|1|0|1|9672|
|com.shatteredpixel.shatteredpixeldungeon|0|0|0|10851|
|com.zoffcc.applications.trifa|0|22|0|20929|
|sum|238|137|236|2221567 (15s on average; 174s max)|

#### 3.5 eAsyncChecker (Timeout: 5min)
|App|HTR|INR|NTT|Time(ms)|
|-|-|-|-|-|
|app.fedilab.fedilabtube|0|0|0|300018|
|app.fedilab.lite|0|0|0|300020|
|app.fedilab.mobilizon|0|0|0|300017|
|app.fedilab.tubelab|0|0|0|300016|
|apps.amine.bou.readerforselfoss|0|0|0|300023|
|at.jclehner.rxdroid|0|0|0|300019|
|ch.hgdev.toposuite|1|0|1|300017|
|com.alienpants.leafpicrevived|0|0|0|300017|
|com.apk.editor|0|0|0|300018|
|com.appmindlab.nano|0|0|0|300015|
|com.asdoi.gymwen|0|0|0|300016|
|com.brentpanther.bitcoinwidget|0|0|0|300020|
|com.calcitem.sanmill|0|0|0|300021|
|com.cg.lrceditor|0|0|0|300019|
|com.cweb.messenger|0|0|0|300018|
|com.dar.nclientv2|0|0|0|300012|
|com.dosse.airpods|0|0|0|300016|
|com.dosse.bwentrain.androidPlayer|1|5|1|300016|
|com.dosse.dozeoff|0|0|0|1317|
|com.dozingcatsoftware.bouncy|0|0|0|300019|
|com.farmerbb.taskbar|0|0|0|300019|
|com.fr3ts0n.ecu.gui.androbd|0|0|0|300022|
|com.freerdp.afreerdp|0|0|0|300018|
|com.gh4a|0|0|0|300018|
|com.ghostsq.commander|1|1|1|300026|
|com.gimranov.zandy.app|0|0|0|300016|
|com.github.axet.audiorecorder|0|1|0|300019|
|com.github.axet.bookreader|0|0|0|300019|
|com.github.axet.torrentclient|0|0|0|8673|
|com.github.cvzi.darkmodewallpaper|0|0|0|300022|
|com.github.gschwind.fiddle_assistant|0|0|0|300021|
|com.haringeymobile.ukweather|0|0|0|300020|
|com.iatfei.streakalarm|0|0|0|300020|
|com.ichi2.anki|0|0|0|300028|
|com.igisw.openmoneybox|0|0|0|300017|
|com.jovial.jrpn|1|1|0|300025|
|com.kylecorry.trail_sense|0|0|0|300016|
|com.limbo.emu.main|0|0|0|300017|
|com.limelight|0|0|0|300015|
|com.machiav3lli.backup|0|0|0|300019|
|com.machiav3lli.derdiedas|0|0|0|300021|
|com.mendhak.gpslogger|0|0|0|300018|
|com.mikifus.padland|0|0|0|300019|
|com.nextcloud.android.beta|0|0|0|300018|
|com.nextcloud.client|0|0|0|300019|
|com.oF2pks.classyshark3xodus|0|0|0|300021|
|com.ominous.quickweather|0|0|0|300016|
|com.osfans.trime|0|0|0|300019|
|com.owncloud.android|0|0|0|300017|
|com.perflyst.twire|0|0|0|300017|
|com.poupa.attestationdeplacement|0|0|0|300023|
|com.sapuseven.untis|0|0|0|300019|
|com.securefilemanager.app|0|0|0|300021|
|com.shatteredpixel.shatteredpixeldungeon|0|0|0|300021|
|com.simplemobiletools.gallery.pro|0|0|0|300017|
|com.smartpack.kernelmanager|0|0|0|300017|
|com.smartpack.kernelprofiler|0|0|0|300019|
|com.smartpack.packagemanager|0|0|0|300019|
|com.smartpack.scriptmanager|0|0|0|300015|
|com.smartpack.smartflasher|0|0|0|300020|
|com.spisoft.quicknote|1|0|1|300016|
|com.swordfish.lemuroid|0|0|0|300016|
|com.tnibler.cryptocam|0|0|0|300018|
|com.tunerly|0|0|0|300017|
|com.ubergeek42.WeechatAndroid|0|0|0|10203|
|com.wireguard.android|0|0|0|300016|
|com.zoffcc.applications.trifa|0|0|0|300018|
|de.blau.android|0|0|0|300020|
|de.corona.tracing|0|0|0|300018|
|de.dennisguse.opentracks|0|0|0|300016|
|de.drhoffmannsoftware.calcvac|0|0|0|6466|
|de.eidottermihi.raspicheck|0|0|0|300019|
|de.fff.ccgt|0|0|0|300023|
|de.freehamburger|0|0|0|300019|
|de.luhmer.owncloudnewsreader|0|0|0|300021|
|de.monocles.chat|0|0|0|300019|
|de.nulide.findmydevice|0|0|0|300022|
|de.pixart.messenger|0|0|0|300018|
|de.reimardoeffinger.quickdic|1|0|1|300020|
|de.rochefort.childmonitor|2|0|1|4193|
|de.rwth_aachen.phyphox|0|0|0|300019|
|de.storchp.opentracks.osmplugin|0|0|0|300020|
|de.storchp.opentracks.osmplugin.offline|0|0|0|300024|
|de.sudoq|0|0|0|300022|
|de.syss.MifareClassicTool|0|0|0|300019|
|de.tadris.fitness|0|0|0|300021|
|dev.corruptedark.openchaoschess|0|0|0|300020|
|dev.msfjarvis.aps|0|0|0|300017|
|dnsfilter.android|0|0|0|300020|
|espero.jiofibatterynotifier|0|0|0|300020|
|eu.siacs.conversations|0|0|0|300018|
|eu.sum7.conversations|0|0|0|300018|
|eu.veldsoft.ithaka.board.game|0|0|0|300020|
|fr.gouv.android.stopcovid|0|0|0|300019|
|fr.gouv.etalab.mastodon|0|0|0|300017|
|godau.fynn.usagedirect|1|0|1|300018|
|godau.fynn.usagedirect.system|1|0|1|300019|
|im.quicksy.client|0|0|0|300018|
|im.status.ethereum|0|0|0|300017|
|io.github.chronosx88.yggdrasil|0|0|0|300026|
|io.github.datastopwatch|0|0|0|300022|
|io.github.folderlogs|0|0|0|300023|
|io.github.installalogs|0|0|0|300017|
|io.github.muntashirakon.AppManager|0|0|0|300020|
|io.github.randomfilemaker|0|0|0|300019|
|io.github.saveastxt|0|0|0|300020|
|io.github.silinote|0|0|0|300024|
|io.github.subhamtyagi.ocr|0|0|0|300021|
|is.xyz.omw|0|0|0|300017|
|it.niedermann.nextcloud.deck|0|0|0|300019|
|it.niedermann.owncloud.notes|0|0|0|300018|
|kiwi.root.an2linuxclient|0|0|0|300024|
|la.daube.photochiotte|0|0|0|300021|
|ltd.evilcorp.atox|0|0|0|300018|
|lu.fisch.canze|0|0|0|300018|
|me.blog.korn123.easydiary|0|0|0|300016|
|me.tripsit.tripmobile|0|0|0|300019|
|menion.android.whereyougo|1|0|1|300021|
|mobi.omegacentauri.SendReduced|0|0|0|300020|
|namlit.siteswapgenerator|3|0|3|300010|
|net.bible.android.activity|0|0|0|300018|
|net.i2p.android.router|0|0|0|300019|
|net.justdave.nwsweatheralertswidget|1|0|1|6169|
|net.kollnig.missioncontrol.fdroid|0|0|0|300019|
|net.ktnx.mobileledger|0|0|0|300018|
|net.nhiroki.bluelineconsole|0|0|0|300019|
|net.osmand.plus|0|0|0|252712|
|net.pfiers.osmfocus|0|0|0|300024|
|net.sourceforge.opencamera|2|1|2|300019|
|net.sourceforge.x11basic|0|0|0|300022|
|nodomain.freeyourgadget.gadgetbridge|0|0|0|300019|
|org.snikket.android|0|0|0|300021|
|player.efis.cfd|0|0|0|4323|
|player.efis.mfd|0|0|0|4393|
|posidon.launcher|0|0|0|300019|
|ru.yanus171.feedexfork|0|0|0|300023|
|se.lublin.mumla|0|0|0|300020|
|security.pEp|0|0|0|300019|
|site.leos.apps.lespas|0|0|0|300019|
|su.sadrobot.yashlang|0|0|0|300017|
|sushi.hardcore.droidfs|0|0|0|300018|
|t20kdc.offlinepuzzlesolver|0|0|0|3672|
|theredspy15.ltecleanerfoss|0|0|0|300023|
|top.donmor.tiddloid|0|0|0|300020|
|vocabletrainer.heinecke.aron.vocabletrainer|0|0|0|300019|
|xyz.myachin.downloader|1|0|0|300019|
|xyz.myachin.saveto|0|0|0|300017|
|Sum|18|9|15|-|

#### 3.6 eAsyncChecker (Timeout: 30min)
|App|HTR|INR|NTT|Time(ms)|
|-|-|-|-|-|
|app.fedilab.fedilabtube|0|0|0|1800015|
|app.fedilab.lite|0|0|0|1800017|
|app.fedilab.mobilizon|0|0|0|1800018|
|app.fedilab.tubelab|0|0|0|1800019|
|apps.amine.bou.readerforselfoss|0|0|0|1800023|
|at.jclehner.rxdroid|0|0|0|1800019|
|ch.hgdev.toposuite|1|0|1|1800018|
|com.alienpants.leafpicrevived|0|0|0|1800018|
|com.apk.editor|0|0|0|1800018|
|com.appmindlab.nano|0|0|0|1800020|
|com.asdoi.gymwen|0|0|0|1800020|
|com.brentpanther.bitcoinwidget|0|0|0|1800022|
|com.calcitem.sanmill|0|0|0|1800020|
|com.cg.lrceditor|0|0|0|1800018|
|com.cweb.messenger|0|0|0|1800017|
|com.dar.nclientv2|0|0|0|1800016|
|com.dosse.airpods|0|0|0|1800016|
|com.dosse.bwentrain.androidPlayer|1|5|1|1800016|
|com.dosse.dozeoff|0|0|0|1265|
|com.dozingcatsoftware.bouncy|0|0|0|1800020|
|com.farmerbb.taskbar|0|0|0|1800013|
|com.fr3ts0n.ecu.gui.androbd|0|0|0|1800023|
|com.freerdp.afreerdp|0|0|0|1800019|
|com.gh4a|0|0|0|1800018|
|com.ghostsq.commander|2|3|2|1800016|
|com.gimranov.zandy.app|0|0|0|1800019|
|com.github.axet.audiorecorder|0|2|0|1800018|
|com.github.axet.bookreader|0|0|0|1800020|
|com.github.axet.torrentclient|0|0|0|8456|
|com.github.cvzi.darkmodewallpaper|0|0|0|1800018|
|com.github.gschwind.fiddle_assistant|0|0|0|1800023|
|com.haringeymobile.ukweather|0|0|0|1800018|
|com.iatfei.streakalarm|0|0|0|1800023|
|com.ichi2.anki|0|0|0|1800015|
|com.igisw.openmoneybox|0|0|0|1800020|
|com.jovial.jrpn|1|1|1|1800023|
|com.kylecorry.trail_sense|0|0|0|1800018|
|com.limbo.emu.main|0|0|0|1800015|
|com.limelight|0|0|0|1800019|
|com.machiav3lli.backup|0|0|0|1800020|
|com.machiav3lli.derdiedas|0|0|0|1800018|
|com.mendhak.gpslogger|0|0|0|1800021|
|com.mikifus.padland|0|0|0|1800020|
|com.nextcloud.android.beta|0|0|0|1800018|
|com.nextcloud.client|0|0|0|1800020|
|com.oF2pks.classyshark3xodus|0|0|0|1800021|
|com.ominous.quickweather|0|0|0|1800030|
|com.osfans.trime|0|0|0|1800016|
|com.owncloud.android|0|0|0|1800018|
|com.perflyst.twire|0|0|0|1800017|
|com.poupa.attestationdeplacement|0|0|0|1800024|
|com.sapuseven.untis|0|0|0|1800020|
|com.securefilemanager.app|0|0|0|1800018|
|com.shatteredpixel.shatteredpixeldungeon|0|0|0|1800015|
|com.simplemobiletools.gallery.pro|0|0|0|746800|
|com.smartpack.kernelmanager|0|0|0|1800018|
|com.smartpack.kernelprofiler|0|0|0|1800019|
|com.smartpack.packagemanager|0|0|0|1800021|
|com.smartpack.scriptmanager|0|0|0|1800021|
|com.smartpack.smartflasher|0|0|0|1800021|
|com.spisoft.quicknote|1|0|1|1800018|
|com.swordfish.lemuroid|0|0|0|1800018|
|com.tnibler.cryptocam|0|0|0|1800015|
|com.tunerly|0|0|0|1800024|
|com.ubergeek42.WeechatAndroid|0|0|0|10095|
|com.wireguard.android|0|0|0|1800018|
|com.zoffcc.applications.trifa|0|0|0|1800017|
|de.blau.android|0|0|0|1800028|
|de.corona.tracing|0|0|0|1800033|
|de.dennisguse.opentracks|0|0|0|1800015|
|de.drhoffmannsoftware.calcvac|0|0|0|6955|
|de.eidottermihi.raspicheck|0|0|0|1800015|
|de.fff.ccgt|0|0|0|1800023|
|de.freehamburger|0|0|0|1800018|
|de.luhmer.owncloudnewsreader|0|0|0|1800020|
|de.monocles.chat|0|0|0|1800019|
|de.nulide.findmydevice|0|0|0|1800020|
|de.pixart.messenger|0|0|0|1800024|
|de.reimardoeffinger.quickdic|1|0|0|1800015|
|de.rochefort.childmonitor|2|0|1|4185|
|de.rwth_aachen.phyphox|0|0|0|1800020|
|de.storchp.opentracks.osmplugin.offline|0|0|0|1800030|
|de.storchp.opentracks.osmplugin|0|0|0|1800021|
|de.sudoq|0|0|0|1800027|
|de.syss.MifareClassicTool|0|0|0|1800021|
|de.tadris.fitness|0|0|0|1800017|
|dev.corruptedark.openchaoschess|0|0|0|1800009|
|dev.msfjarvis.aps|0|0|0|1800020|
|dnsfilter.android|0|0|0|1800023|
|espero.jiofibatterynotifier|0|0|0|1800025|
|eu.siacs.conversations|0|0|0|1800019|
|eu.sum7.conversations|0|0|0|1800019|
|eu.veldsoft.ithaka.board.game|0|0|0|459414|
|fr.gouv.android.stopcovid|0|0|0|1800016|
|fr.gouv.etalab.mastodon|0|0|0|1800018|
|godau.fynn.usagedirect.system|1|0|1|1800021|
|godau.fynn.usagedirect|1|0|1|1800020|
|im.quicksy.client|0|0|0|1800020|
|im.status.ethereum|0|0|0|1800017|
|io.github.chronosx88.yggdrasil|0|0|0|1800019|
|io.github.datastopwatch|0|0|0|1800019|
|io.github.folderlogs|0|0|0|1800017|
|io.github.installalogs|0|0|0|1800019|
|io.github.muntashirakon.AppManager|0|0|0|1800021|
|io.github.randomfilemaker|0|0|0|1800019|
|io.github.saveastxt|0|0|0|1800024|
|io.github.silinote|0|0|0|1800021|
|io.github.subhamtyagi.ocr|0|0|0|1800020|
|is.xyz.omw|0|0|0|1800017|
|it.niedermann.nextcloud.deck|0|0|0|1800021|
|it.niedermann.owncloud.notes|0|0|0|1800020|
|kiwi.root.an2linuxclient|0|0|0|1800019|
|la.daube.photochiotte|0|0|0|1800017|
|ltd.evilcorp.atox|0|0|0|1800021|
|lu.fisch.canze|0|0|0|1800020|
|me.blog.korn123.easydiary|0|0|0|1800016|
|me.tripsit.tripmobile|0|0|0|1800019|
|menion.android.whereyougo|1|0|1|1800020|
|mobi.omegacentauri.SendReduced|0|0|0|1800018|
|namlit.siteswapgenerator|3|0|3|1800021|
|net.bible.android.activity|0|0|0|1800022|
|net.i2p.android.router|0|0|0|1800017|
|net.justdave.nwsweatheralertswidget|1|0|1|6179|
|net.kollnig.missioncontrol.fdroid|0|0|0|1800018|
|net.ktnx.mobileledger|0|0|0|1800017|
|net.nhiroki.bluelineconsole|0|0|0|1800021|
|net.osmand.plus|0|0|0|299635|
|net.pfiers.osmfocus|0|0|0|1800019|
|net.sourceforge.opencamera|2|1|2|1800019|
|net.sourceforge.x11basic|0|0|0|942087|
|nodomain.freeyourgadget.gadgetbridge|0|0|0|1800017|
|org.snikket.android|0|0|0|1800018|
|player.efis.cfd|0|0|0|4351|
|player.efis.mfd|0|0|0|4505|
|posidon.launcher|0|0|0|1800017|
|ru.yanus171.feedexfork|0|0|0|1800015|
|se.lublin.mumla|0|0|0|1800021|
|security.pEp|0|0|0|1800018|
|site.leos.apps.lespas|0|0|0|1800019|
|su.sadrobot.yashlang|0|0|0|1800040|
|sushi.hardcore.droidfs|0|0|0|1800017|
|t20kdc.offlinepuzzlesolver|0|0|0|3673|
|theredspy15.ltecleanerfoss|0|0|0|1800025|
|top.donmor.tiddloid|0|0|0|1800020|
|vocabletrainer.heinecke.aron.vocabletrainer|0|0|0|1800018|
|xyz.myachin.downloader|1|0|0|1800027|
|xyz.myachin.saveto|0|0|0|1800017|
|Sum|19|12|16|-|
