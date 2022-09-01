# Leopard

A lightweight tool to detect thread related misuses in Java.

In the Java platform, it is common to use the fundamental asynchronous thread (java.lang.Thread). However, due to garbage collection and thread interruption mechanism, it can be easily misused. The recycle of an object will be blocked if an alive thread holds a strong reference to it. In addition, the careless implementation of asynchronous thread may cause that there is not responding to the interrupt mechanism. This may result the unexpected thread related behavior and resource leak/waste.

we implement a lightweight tool named Leopard to detect them statically, which is a very fast one with the demand-driven slicing analysis. It reduces false negatives caused by the heavyweight time-consuming path sensitive analysis proposed by existing work.


## Benchmark

###  Java Programs
|project|version|link|
|-|-|-|
|apache-cxf|3.5.2|[Go](https://repo1.maven.org/maven2/org/apache/cxf/cxf-core/3.5.2/)|
|apache-tomcat|10.0.20|[Go](https://github.com/cuixiaoyiyi/Leopard/tree/main/Benchmark/JavaPrograms/apache-tomcat-10.0.20)|
|micronaut-cli|3.4.2|[Go](https://github.com/cuixiaoyiyi/Leopard/tree/main/Benchmark/JavaPrograms/micronaut-cli-3.4.2)|
|hivemq-ce|2021.3|[Go](https://github.com/hivemq/hivemq-community-edition/releases/download/2021.3/hivemq-ce-2021.3.zip)|
|jetty-distribution|9.4.46.v20220331|[Go](https://github.com/cuixiaoyiyi/Leopard/tree/main/Benchmark/JavaPrograms/jetty-distribution-9.4.46.v20220331)|
|Junit5|-|[Go](https://github.com/cuixiaoyiyi/Leopard/tree/main/Benchmark/JavaPrograms/Junit5)|
|quarkus-cli|2.8.0.Final|[Go](https://github.com/quarkusio/quarkus/releases/download/2.8.0.Final/quarkus-cli-2.8.0.Final.zip)|
|SpringFramework|5.3.18|[Go](https://mvnrepository.com/artifact/org.springframework/spring/5.3.18)|
|wildfly|26.1.0.Beta1|[Go](https://github.com/wildfly/wildfly/releases/download/26.1.0.Final/wildfly-26.1.0.Final.zip)|

### Android Apps
download date : 2021-09-21 from F-Droid
<details>
<summary>APKs (Click for unfolding)</summary>
<pre><code>
app.fedilab.fedilabtube.apk
app.fedilab.lite.apk
app.fedilab.mobilizon.apk
app.fedilab.tubelab.apk
apps.amine.bou.readerforselfoss.apk
at.jclehner.rxdroid.apk
ch.hgdev.toposuite.apk
com.alienpants.leafpicrevived.apk
com.apk.editor.apk
com.appmindlab.nano.apk
com.asdoi.gymwen.apk
com.brentpanther.bitcoinwidget.apk
com.calcitem.sanmill.apk
com.cg.lrceditor.apk
com.cweb.messenger.apk
com.dar.nclientv2.apk
com.dosse.airpods.apk
com.dosse.bwentrain.androidPlayer.apk
com.dosse.dozeoff.apk
com.dozingcatsoftware.bouncy.apk
com.farmerbb.taskbar.apk
com.fr3ts0n.ecu.gui.androbd.apk
com.freerdp.afreerdp.apk
com.gh4a.apk
com.ghostsq.commander.apk
com.gimranov.zandy.app.apk
com.github.axet.audiorecorder.apk
com.github.axet.bookreader.apk
com.github.axet.torrentclient.apk
com.github.cvzi.darkmodewallpaper.apk
com.github.gschwind.fiddle_assistant.apk
com.haringeymobile.ukweather.apk
com.iatfei.streakalarm.apk
com.ichi2.anki.apk
com.igisw.openmoneybox.apk
com.jovial.jrpn.apk
com.kylecorry.trail_sense.apk
com.limbo.emu.main.apk
com.limelight.apk
com.machiav3lli.backup.apk
com.machiav3lli.derdiedas.apk
com.mendhak.gpslogger.apk
com.mikifus.padland.apk
com.nextcloud.android.beta.apk
com.nextcloud.client.apk
com.oF2pks.classyshark3xodus.apk
com.ominous.quickweather.apk
com.osfans.trime.apk
com.owncloud.android.apk
com.perflyst.twire.apk
com.poupa.attestationdeplacement.apk
com.sapuseven.untis.apk
com.securefilemanager.app.apk
com.shatteredpixel.shatteredpixeldungeon.apk
com.simplemobiletools.gallery.pro.apk
com.smartpack.kernelmanager.apk
com.smartpack.kernelprofiler.apk
com.smartpack.packagemanager.apk
com.smartpack.scriptmanager.apk
com.smartpack.smartflasher.apk
com.spisoft.quicknote.apk
com.swordfish.lemuroid.apk
com.tnibler.cryptocam.apk
com.tunerly.apk
com.ubergeek42.WeechatAndroid.apk
com.wireguard.android.apk
com.zoffcc.applications.trifa.apk
de.blau.android.apk
de.corona.tracing.apk
de.dennisguse.opentracks.apk
de.drhoffmannsoftware.calcvac.apk
de.eidottermihi.raspicheck.apk
de.fff.ccgt.apk
de.freehamburger.apk
de.luhmer.owncloudnewsreader.apk
de.monocles.chat.apk
de.nulide.findmydevice.apk
de.pixart.messenger.apk
de.reimardoeffinger.quickdic.apk
de.rochefort.childmonitor.apk
de.rwth_aachen.phyphox.apk
de.storchp.opentracks.osmplugin.apk
de.storchp.opentracks.osmplugin.offline.apk
de.sudoq.apk
de.syss.MifareClassicTool.apk
de.tadris.fitness.apk
dev.corruptedark.openchaoschess.apk
dev.msfjarvis.aps.apk
dnsfilter.android.apk
espero.jiofibatterynotifier.apk
eu.siacs.conversations.apk
eu.sum7.conversations.apk
eu.veldsoft.ithaka.board.game.apk
fr.gouv.android.stopcovid.apk
fr.gouv.etalab.mastodon.apk
godau.fynn.usagedirect.apk
godau.fynn.usagedirect.system.apk
im.quicksy.client.apk
im.status.ethereum.apk
io.github.chronosx88.yggdrasil.apk
io.github.datastopwatch.apk
io.github.folderlogs.apk
io.github.installalogs.apk
io.github.muntashirakon.AppManager.apk
io.github.randomfilemaker.apk
io.github.saveastxt.apk
io.github.silinote.apk
io.github.subhamtyagi.ocr.apk
is.xyz.omw.apk
it.niedermann.nextcloud.deck.apk
it.niedermann.owncloud.notes.apk
kiwi.root.an2linuxclient.apk
la.daube.photochiotte.apk
ltd.evilcorp.atox.apk
lu.fisch.canze.apk
me.blog.korn123.easydiary.apk
me.tripsit.tripmobile.apk
menion.android.whereyougo.apk
mobi.omegacentauri.SendReduced.apk
namlit.siteswapgenerator.apk
net.bible.android.activity.apk
net.i2p.android.router.apk
net.justdave.nwsweatheralertswidget.apk
net.kollnig.missioncontrol.fdroid.apk
net.ktnx.mobileledger.apk
net.nhiroki.bluelineconsole.apk
net.osmand.plus.apk
net.pfiers.osmfocus.apk
net.sourceforge.opencamera.apk
net.sourceforge.x11basic.apk
nodomain.freeyourgadget.gadgetbridge.apk
org.snikket.android.apk
player.efis.cfd.apk
player.efis.mfd.apk
posidon.launcher.apk
ru.yanus171.feedexfork.apk
se.lublin.mumla.apk
security.pEp.apk
site.leos.apps.lespas.apk
su.sadrobot.yashlang.apk
sushi.hardcore.droidfs.apk
t20kdc.offlinepuzzlesolver.apk
theredspy15.ltecleanerfoss.apk
top.donmor.tiddloid.apk
vocabletrainer.heinecke.aron.vocabletrainer.apk
xyz.myachin.downloader.apk
xyz.myachin.saveto.apk
</code></pre>
</details>

## Result

### Java Programs (without FP refinement)
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

### Java Programs (with FP refinement)
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

#### Leopard 
|App|HTR|INR|NTT|Time(ms)|
|-|-|-|-|-|
|net.sourceforge.opencamera|2|1|2|10272|
|com.github.axet.torrentclient|0|1|0|10101|
|se.lublin.mumla|1|0|1|33143|
|com.igisw.openmoneybox|1|0|1|12498|
|com.freerdp.afreerdp|1|0|1|7969|
|org.snikket.android|2|0|2|17969|
|lu.fisch.canze|13|1|13|13899|
|it.niedermann.owncloud.notes|2|0|2|19927|
|espero.jiofibatterynotifier|1|0|1|6395|
|xyz.myachin.saveto|2|0|2|8201|
|com.fr3ts0n.ecu.gui.androbd|0|1|0|9563|
|posidon.launcher|0|1|0|20097|
|fr.gouv.etalab.mastodon|9|2|9|107904|
|is.xyz.omw|0|0|0|11591|
|com.farmerbb.taskbar|3|0|3|10201|
|ru.yanus171.feedexfork|2|20|2|16496|
|net.i2p.android.router|0|2|0|39831|
|net.osmand.plus|2|2|2|188318|
|menion.android.whereyougo|2|1|2|10902|
|de.corona.tracing|0|0|0|21688|
|t20kdc.offlinepuzzlesolver|1|1|1|4211|
|player.efis.cfd|0|0|0|4492|
|player.efis.mfd|0|0|0|4513|
|com.gh4a|0|0|0|25224|
|io.github.chronosx88.yggdrasil|0|1|0|14455|
|io.github.installalogs|1|0|1|10952|
|de.drhoffmannsoftware.calcvac|0|0|0|4416|
|de.eidottermihi.raspicheck|0|0|0|18400|
|com.haringeymobile.ukweather|2|0|2|6054|
|io.github.subhamtyagi.ocr|1|1|1|7961|
|mobi.omegacentauri.SendReduced|0|0|0|4422|
|io.github.muntashirakon.AppManager|5|0|5|22541|
|site.leos.apps.lespas|1|0|1|19943|
|vocabletrainer.heinecke.aron.vocabletrainer|1|0|1|9026|
|com.dar.nclientv2|3|5|3|15522|
|com.iatfei.streakalarm|0|0|0|15265|
|com.github.cvzi.darkmodewallpaper|2|2|2|12985|
|namlit.siteswapgenerator|9|0|9|9549|
|xyz.myachin.downloader|1|0|1|8260|
|net.sourceforge.x11basic|0|0|0|13915|
|com.kylecorry.trail_sense|0|0|0|12655|
|de.storchp.opentracks.osmplugin|1|1|1|11909|
|sushi.hardcore.droidfs|1|0|1|12698|
|com.mendhak.gpslogger|0|0|0|22541|
|com.cg.lrceditor|12|0|12|7154|
|com.smartpack.kernelmanager|3|3|3|13237|
|com.dozingcatsoftware.bouncy|0|0|0|7331|
|app.fedilab.tubelab|18|0|18|28327|
|theredspy15.ltecleanerfoss|2|0|2|7972|
|at.jclehner.rxdroid|1|5|1|13056|
|app.fedilab.mobilizon|2|0|2|11806|
|com.perflyst.twire|1|0|1|15191|
|com.limbo.emu.main|20|1|20|11685|
|de.luhmer.owncloudnewsreader|0|1|0|25654|
|com.smartpack.smartflasher|1|1|1|8054|
|com.poupa.attestationdeplacement|2|0|2|30166|
|kiwi.root.an2linuxclient|0|0|0|8936|
|dev.msfjarvis.aps|0|0|0|15181|
|ch.hgdev.toposuite|4|0|4|8375|
|ltd.evilcorp.atox|0|0|0|11630|
|net.pfiers.osmfocus|0|0|0|12958|
|com.machiav3lli.backup|2|0|2|10550|
|com.gimranov.zandy.app|1|0|1|7275|
|eu.siacs.conversations|2|0|2|19316|
|io.github.folderlogs|1|0|1|11813|
|de.tadris.fitness|3|1|3|44311|
|de.syss.MifareClassicTool|2|0|2|9595|
|com.calcitem.sanmill|0|0|0|6681|
|io.github.saveastxt|1|0|1|9401|
|de.nulide.findmydevice|0|2|0|15229|
|com.ichi2.anki|0|1|0|30581|
|com.sapuseven.untis|0|1|0|14208|
|com.tunerly|0|0|0|14647|
|de.freehamburger|0|3|0|12621|
|com.limelight|2|1|2|6771|
|net.justdave.nwsweatheralertswidget|1|0|1|6839|
|com.github.gschwind.fiddle_assistant|0|0|0|8610|
|de.fff.ccgt|1|0|1|11447|
|godau.fynn.usagedirect|2|0|2|7870|
|de.rwth_aachen.phyphox|0|0|0|12814|
|com.mikifus.padland|0|0|0|10481|
|net.ktnx.mobileledger|0|2|0|15759|
|com.github.axet.audiorecorder|0|3|0|9329|
|com.securefilemanager.app|0|0|0|12664|
|de.blau.android|0|1|0|27596|
|la.daube.photochiotte|0|0|0|10328|
|me.blog.korn123.easydiary|1|0|1|17774|
|top.donmor.tiddloid|0|0|0|11330|
|com.spisoft.quicknote|1|1|1|12391|
|com.smartpack.packagemanager|2|2|2|9483|
|im.status.ethereum|0|0|0|41797|
|de.sudoq|1|1|1|11523|
|net.kollnig.missioncontrol.fdroid|2|0|2|16500|
|com.swordfish.lemuroid|0|0|0|13351|
|com.apk.editor|1|1|1|9950|
|com.ominous.quickweather|0|0|0|9090|
|com.nextcloud.android.beta|8|1|8|124225|
|io.github.randomfilemaker|1|0|1|11566|
|com.brentpanther.bitcoinwidget|0|0|0|8527|
|com.ghostsq.commander|2|3|2|6881|
|com.nextcloud.client|8|1|8|124296|
|godau.fynn.usagedirect.system|1|0|1|7935|
|dev.corruptedark.openchaoschess|4|3|4|7167|
|com.cweb.messenger|2|0|2|20656|
|apps.amine.bou.readerforselfoss|0|1|0|13781|
|com.wireguard.android|0|0|0|11371|
|com.smartpack.scriptmanager|2|2|2|7735|
|com.jovial.jrpn|2|1|2|7822|
|nodomain.freeyourgadget.gadgetbridge|1|0|1|33152|
|com.oF2pks.classyshark3xodus|3|2|3|23932|
|com.dosse.airpods|0|0|0|7026|
|com.owncloud.android|0|0|0|26807|
|it.niedermann.nextcloud.deck|0|0|0|27383|
|me.tripsit.tripmobile|0|0|0|10135|
|fr.gouv.android.stopcovid|0|0|0|62867|
|net.nhiroki.bluelineconsole|4|0|2|7658|
|de.storchp.opentracks.osmplugin.offline|1|1|1|11778|
|com.ubergeek42.WeechatAndroid|0|1|0|11816|
|com.smartpack.kernelprofiler|1|1|1|7623|
|de.monocles.chat|6|0|6|93922|
|app.fedilab.lite|9|2|9|95396|
|com.asdoi.gymwen|7|0|7|18102|
|com.simplemobiletools.gallery.pro|0|0|0|18219|
|im.quicksy.client|2|0|2|19985|
|com.appmindlab.nano|4|4|4|10900|
|com.dosse.bwentrain.androidPlayer|1|3|1|7567|
|com.alienpants.leafpicrevived|1|0|1|13169|
|com.dosse.dozeoff|0|0|0|4246|
|su.sadrobot.yashlang|17|0|17|12843|
|io.github.datastopwatch|1|0|1|11429|
|de.reimardoeffinger.quickdic|1|0|1|8157|
|com.osfans.trime|0|0|0|4879|
|eu.veldsoft.ithaka.board.game|2|0|2|9066|
|com.machiav3lli.derdiedas|2|0|2|9025|
|com.tnibler.cryptocam|0|0|0|10831|
|com.github.axet.bookreader|1|3|1|10722|
|net.bible.android.activity|0|0|0|16455|
|de.pixart.messenger|6|0|6|93512|
|de.dennisguse.opentracks|2|0|2|14505|
|security.pEp|0|1|0|42399|
|dnsfilter.android|2|0|2|6197|
|de.rochefort.childmonitor|2|0|2|5160|
|eu.sum7.conversations|2|0|2|18994|
|app.fedilab.fedilabtube|18|0|18|29061|
|io.github.silinote|1|0|1|11994|
|com.shatteredpixel.shatteredpixeldungeon|0|0|0|7596|
|com.zoffcc.applications.trifa|14|24|14|25906|
|Sum|305|128|303|max(188318),average(19169)|

#### eAsyncChecker (Timeout: 5min)
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

#### eAsyncChecker (Timeout: 30min)
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


### Case Study
package namlit.siteswapgenerator;
```
public class MainActivity extends AppCompatActivity implements AddFilterDialog.FilterDialogListener, LoadGenerationParametersDialog.UpdateGenerationParameters {
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        int i = paramMenuItem.getItemId();
        if (i == 2131296281) {
          loadGenerationParameters();
        } else if (i == 2131296290) {
          saveGenerationParameters();
        } else if (i == 2131296273) {
          deleteGenerationParameters();
        } else if (i == 2131296287) {
          showNamedSiteswaps();
        } else if (i == 2131296275) {
          exportAppDatabase();
        } else if (i == 2131296280) {
          importAppDatabase();
        } else if (i == 2131296276) {
          favorites();
        } else if (i == 2131296262) {
          showAboutDialog();
        } else if (i == 2131296278) {
          AlertDialog.Builder builder = new AlertDialog.Builder((Context)this);
          builder.setMessage((CharSequence)Html.fromHtml(getString(2131689588))).setNeutralButton(getString(2131689521), null);
          builder.create().show();
        } 
        return super.onOptionsItemSelected(paramMenuItem);
  }
  
    public void saveGenerationParameters() {
        GenerationParameterEntity generationParameterEntity = new GenerationParameterEntity();
        if (!updateFromTextEdits())
          return; 
        generationParameterEntity.setNumberOfObjects(this.mNumberOfObjects);
        generationParameterEntity.setPeriodLength(this.mPeriodLength);
        generationParameterEntity.setMaxThrow(this.mMaxThrow);
        generationParameterEntity.setMinThrow(this.mMinThrow);
        generationParameterEntity.setNumberOfJugglers(this.mNumberOfJugglers);
        generationParameterEntity.setMaxResults(this.mMaxResults);
        generationParameterEntity.setTimeout(this.mTimeout);
        generationParameterEntity.setSynchronous(this.mIsSyncPattern);
        generationParameterEntity.setRandomMode(this.mIsRandomGenerationMode);
        generationParameterEntity.setZips(this.mIsZips);
        generationParameterEntity.setZaps(this.mIsZaps);
        generationParameterEntity.setHolds(this.mIsHolds);
        generationParameterEntity.setFilterList(this.mFilterList);
        (new SaveGenerationParametersDialog()).show(getSupportFragmentManager(), getString(2131689706), generationParameterEntity);
  }
}
```
package namlit.siteswapgenerator;
```
public class SaveGenerationParametersDialog extends DialogFragment {
    public Dialog onCreateDialog(Bundle paramBundle) {
        if (paramBundle != null)
          this.mGenerationParameterEntity = (GenerationParameterEntity)paramBundle.getSerializable("STATE_GENERATION_PARAMETER_ENTITY"); 
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)getActivity());
        builder.setView(2131492917).setTitle(getString(2131689707)).setNegativeButton(getString(2131689523), new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface param1DialogInterface, int param1Int) {}
            }).setPositiveButton(getString(2131689676), null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
              public void onShow(final DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(-1).setOnClickListener(new View.OnClickListener() {
                      public void onClick(View param2View) {
                        SaveGenerationParametersDialog.this.mGenerationParameterEntity.setName(SaveGenerationParametersDialog.this.mGenerationParameterNameTextEdit.getText().toString());
                        SaveGenerationParametersDialog.this.insertEntityInDatabase();
                        dialog.dismiss();
                      }
                    });
              }
            });
        return (Dialog)alertDialog;
  }
  
    public void insertEntityInDatabase() {
        (new Thread(new Runnable() {
              public void run() {
                try {
                  AppDatabase.getAppDatabase(SaveGenerationParametersDialog.this.getContext()).generationParameterDao().insertGenerationParameters(new GenerationParameterEntity[] { SaveGenerationParametersDialog.access$100(this.this$0) });
                } catch (SQLiteConstraintException sQLiteConstraintException) {}
              }
            })).start();
  }
}
```
