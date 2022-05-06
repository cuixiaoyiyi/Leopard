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
|governator|1.17.12|[Go](https://github.com/cuixiaoyiyi/Leopard/tree/main/Benchmark/JavaPrograms/governator-1.17.12)|
|hivemq-ce|2021.3|[Go](https://github.com/hivemq/hivemq-community-edition/releases/download/2021.3/hivemq-ce-2021.3.zip)|
|jetty-distribution|9.4.46.v20220331|[Go](https://github.com/cuixiaoyiyi/Leopard/tree/main/Benchmark/JavaPrograms/jetty-distribution-9.4.46.v20220331)|
|Junit5|-|https://github.com/cuixiaoyiyi/Leopard/tree/main/Benchmark/JavaPrograms/Junit5|
|quarkus-cli|2.8.0.Final|[Go](https://github.com/quarkusio/quarkus/releases/download/2.8.0.Final/quarkus-cli-2.8.0.Final.zip)|
|SpringFramework|5.3.18|[Go](https://mvnrepository.com/artifact/org.springframework/spring/5.3.18)|
|wildfly|26.1.0.Beta1|[Go](https://github.com/wildfly/wildfly/releases/download/26.1.0.Final/wildfly-26.1.0.Final.zip)|

### Android Apps
download date : 2021-09-21 from F-Droid
```
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
```
