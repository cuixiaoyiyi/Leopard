package ac.entrypoint.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import ac.LeopardMain;
import ac.util.InheritanceProcess;
import jymbolic.android.resources.controls.ProcessManifest;
import jymbolic.android.resources.xml.parser.ARSCFileParser;
import jymbolic.android.resources.xml.parser.LayoutFileParser;
import soot.Body;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.Tag;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

public final class AndroidCallBacks {

	public static MultiMap<String, String> sXMLCALLBACK_METHODS = null;
	public static MultiMap<String, SootClass> sXMLFRAGMENTS = null;

	public static ProcessManifest sMANIFEST = null;

	static {
		String apkPath = LeopardMain.getApkFullPath();
		try {
			sMANIFEST = new ProcessManifest(apkPath);
			String packageName = sMANIFEST.getPackageName();
			ARSCFileParser resources = new ARSCFileParser();
			resources.parse(apkPath);
			LayoutFileParser layoutFileParser = new LayoutFileParser(packageName, resources);
			layoutFileParser.parseLayoutFileDirect(apkPath);
			sXMLCALLBACK_METHODS = layoutFileParser.getCallbackMethods();
			sXMLFRAGMENTS = layoutFileParser.getFragments();
		} catch (IOException | XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	private static String[] ANDROID_CALLBACKS = new String[]{
//		"android.view.View$OnClickListener",
//		"android.content.DialogInterface$OnClickListener"
//	};

	private static String[] ANDROID_CALLBACKS = new String[] { "android.accounts.OnAccountsUpdateListener",
			"android.animation.Animator$AnimatorListener", "android.animation.LayoutTransition$TransitionListener",
			"android.animation.TimeAnimator$TimeListener", "android.animation.ValueAnimator$AnimatorUpdateListener",
			"android.app.ActionBar$OnMenuVisibilityListener", "android.app.ActionBar$OnNavigationListener",
			"android.app.ActionBar$TabListener", "android.app.Application$ActivityLifecycleCallbacks",
			"android.app.DatePickerDialog$OnDateSetListener",
			"android.app.FragmentBreadCrumbs$OnBreadCrumbClickListener",
			"android.app.FragmentManager$OnBackStackChangedListener",
			"android.app.KeyguardManager$OnKeyguardExitResult", "android.app.LoaderManager$LoaderCallbacks",
			"android.app.PendingIntent$OnFinished", "android.app.SearchManager$OnCancelListener",
			"android.app.SearchManager$OnDismissListener", "android.app.TimePickerDialog$OnTimeSetListener",
			"android.bluetooth.BluetoothProfile$ServiceListener",
			"android.content.ClipboardManager$OnPrimaryClipChangedListener", "android.content.ComponentCallbacks",
			"android.content.ComponentCallbacks2", "android.content.DialogInterface$OnCancelListener",
			"android.content.DialogInterface$OnClickListener", "android.content.DialogInterface$OnDismissListener",
			"android.content.DialogInterface$OnKeyListener",
			"android.content.DialogInterface$OnMultiChoiceClickListener",
			"android.content.DialogInterface$OnShowListener", "android.content.IntentSender$OnFinished",
			"android.content.Loader$OnLoadCanceledListener", "android.content.Loader$OnLoadCompleteListener",
			"android.content.SharedPreferences$OnSharedPreferenceChangeListener", "android.content.SyncStatusObserver",
			"android.database.sqlite.SQLiteTransactionListener", "android.drm.DrmManagerClient$OnErrorListener",
			"android.drm.DrmManagerClient$OnEventListener", "android.drm.DrmManagerClient$OnInfoListener",
			"android.gesture.GestureOverlayView$OnGestureListener",
			"android.gesture.GestureOverlayView$OnGesturePerformedListener",
			"android.gesture.GestureOverlayView$OnGesturingListener",
			"android.graphics.SurfaceTexture$OnFrameAvailableListener", "android.hardware.Camera$AutoFocusCallback",
			"android.hardware.Camera$AutoFocusMoveCallback", "android.hardware.Camera$ErrorCallback",
			"android.hardware.Camera$FaceDetectionListener", "android.hardware.Camera$OnZoomChangeListener",
			"android.hardware.Camera$PictureCallback", "android.hardware.Camera$PreviewCallback",
			"android.hardware.Camera$ShutterCallback", "android.hardware.SensorEventListener",
			"android.hardware.display.DisplayManager$DisplayListener",
			"android.hardware.input.InputManager$InputDeviceListener",
			"android.inputmethodservice.KeyboardView$OnKeyboardActionListener", "android.location.GpsStatus$Listener",
			"android.location.GpsStatus$NmeaListener", "android.location.LocationListener",
			"android.media.AudioManager$OnAudioFocusChangeListener",
			"android.media.AudioRecord$OnRecordPositionUpdateListener", "android.media.JetPlayer$OnJetEventListener",
			"android.media.MediaPlayer$OnBufferingUpdateListener", "android.media.MediaPlayer$OnCompletionListener",
			"android.media.MediaPlayer$OnErrorListener", "android.media.MediaPlayer$OnInfoListener",
			"android.media.MediaPlayer$OnPreparedListener", "android.media.MediaPlayer$OnSeekCompleteListener",
			"android.media.MediaPlayer$OnTimedTextListener", "android.media.MediaPlayer$OnVideoSizeChangedListener",
			"android.media.MediaRecorder$OnErrorListener", "android.media.MediaRecorder$OnInfoListener",
			"android.media.MediaScannerConnection$MediaScannerConnectionClient",
			"android.media.MediaScannerConnection$OnScanCompletedListener",
			"android.media.SoundPool$OnLoadCompleteListener",
			"android.media.audiofx.AudioEffect$OnControlStatusChangeListener",
			"android.media.audiofx.AudioEffect$OnEnableStatusChangeListener",
			"android.media.audiofx.BassBoost$OnParameterChangeListener",
			"android.media.audiofx.EnvironmentalReverb$OnParameterChangeListener",
			"android.media.audiofx.Equalizer$OnParameterChangeListener",
			"android.media.audiofx.PresetReverb$OnParameterChangeListener",
			"android.media.audiofx.Virtualizer$OnParameterChangeListener",
			"android.media.audiofx.Visualizer$OnDataCaptureListener", "android.media.effect$EffectUpdateListener",
			"android.net.nsd.NsdManager$DiscoveryListener", "android.net.nsd.NsdManager$RegistrationListener",
			"android.net.nsd.NsdManager$ResolveListener", "android.net.sip.SipRegistrationListener",
			"android.net.wifi.p2p.WifiP2pManager$ActionListener", "android.net.wifi.p2p.WifiP2pManager$ChannelListener",
			"android.net.wifi.p2p.WifiP2pManager$ConnectionInfoListener",
			"android.net.wifi.p2p.WifiP2pManager$DnsSdServiceResponseListener",
			"android.net.wifi.p2p.WifiP2pManager$DnsSdTxtRecordListener",
			"android.net.wifi.p2p.WifiP2pManager$GroupInfoListener",
			"android.net.wifi.p2p.WifiP2pManager$PeerListListener",
			"android.net.wifi.p2p.WifiP2pManager$ServiceResponseListener",
			"android.net.wifi.p2p.WifiP2pManager$UpnpServiceResponseListener",
			"android.os.CancellationSignal$OnCancelListener", "android.os.IBinder$DeathRecipient",
			"android.os.MessageQueue$IdleHandler", "android.os.RecoverySystem$ProgressListener",
			"android.preference.Preference$OnPreferenceChangeListener",
			"android.preference.Preference$OnPreferenceClickListener",
			"android.preference.PreferenceFragment$OnPreferenceStartFragmentCallback",
			"android.preference.PreferenceManager$OnActivityDestroyListener",
			"android.preference.PreferenceManager$OnActivityResultListener",
			"android.preference.PreferenceManager$OnActivityStopListener", "android.security.KeyChainAliasCallback",
			"android.speech.RecognitionListener", "android.speech.tts.TextToSpeech$OnInitListener",
			"android.speech.tts.TextToSpeech$OnUtteranceCompletedListener", "android.view.ActionMode$Callback",
			"android.view.ActionProvider$VisibilityListener", "android.view.GestureDetector$OnDoubleTapListener",
			"android.view.GestureDetector$OnGestureListener", "android.view.InputQueue$Callback",
			"android.view.KeyEvent$Callback", "android.view.MenuItem$OnActionExpandListener",
			"android.view.MenuItem$OnMenuItemClickListener", "android.view.ScaleGestureDetector$OnScaleGestureListener",
			"android.view.SurfaceHolder$Callback", "android.view.SurfaceHolder$Callback2",
			"android.view.TextureView$SurfaceTextureListener", "android.view.View$OnAttachStateChangeListener",
			"android.view.View$OnClickListener", "android.view.View$OnCreateContextMenuListener",
			"android.view.View$OnDragListener", "android.view.View$OnFocusChangeListener",
			"android.view.View$OnGenericMotionListener", "android.view.View$OnHoverListener",
			"android.view.View$OnKeyListener", "android.view.View$OnLayoutChangeListener",
			"android.view.View$OnLongClickListener", "android.view.View$OnSystemUiVisibilityChangeListener",
			"android.view.View$OnTouchListener", "android.view.ViewGroup$OnHierarchyChangeListener",
			"android.view.ViewStub$OnInflateListener", "android.view.ViewTreeObserver$OnDrawListener",
			"android.view.ViewTreeObserver$OnGlobalFocusChangeListener",
			"android.view.ViewTreeObserver$OnGlobalLayoutListener", "android.view.ViewTreeObserver$OnPreDrawListener",
			"android.view.ViewTreeObserver$OnScrollChangedListener",
			"android.view.ViewTreeObserver$OnTouchModeChangeListener",
			"android.view.accessibility.AccessibilityManager$AccessibilityStateChangeListener",
			"android.view.animation.Animation$AnimationListener",
			"android.view.inputmethod.InputMethod$SessionCallback",
			"android.view.inputmethod.InputMethodSession$EventCallback",
			"android.view.textservice.SpellCheckerSession$SpellCheckerSessionListener",
			"android.webkit.DownloadListener", "android.widget.AbsListView$MultiChoiceModeListener",
			"android.widget.AbsListView$OnScrollListener", "android.widget.AbsListView$RecyclerListener",
			"android.widget.AdapterView$OnItemClickListener", "android.widget.AdapterView$OnItemLongClickListener",
			"android.widget.AdapterView.OnItemSelectedListener",
			"android.widget.AutoCompleteTextView$OnDismissListener", "android.widget.CalendarView$OnDateChangeListener",
			"android.widget.Chronometer$OnChronometerTickListener",
			"android.widget.CompoundButton$OnCheckedChangeListener", "android.widget.DatePicker$OnDateChangedListener",
			"android.widget.ExpandableListView$OnChildClickListener",
			"android.widget.ExpandableListView$OnGroupClickListener",
			"android.widget.ExpandableListView$OnGroupCollapseListener",
			"android.widget.ExpandableListView$OnGroupExpandListener", "android.widget.Filter$FilterListener",
			"android.widget.NumberPicker$OnScrollListener", "android.widget.NumberPicker$OnValueChangeListener",
			"android.widget.NumberPicker$OnDismissListener", "android.widget.PopupMenu$OnMenuItemClickListener",
			"android.widget.PopupWindow$OnDismissListener", "android.widget.RadioGroup$OnCheckedChangeListener",
			"android.widget.RatingBar$OnRatingBarChangeListener", "android.widget.SearchView$OnCloseListener",
			"android.widget.SearchView$OnQueryTextListener", "android.widget.SearchView$OnSuggestionListener",
			"android.widget.SeekBar$OnSeekBarChangeListener",
			"android.widget.ShareActionProvider$OnShareTargetSelectedListener",
			"android.widget.SlidingDrawer$OnDrawerCloseListener", "android.widget.SlidingDrawer$OnDrawerOpenListener",
			"android.widget.SlidingDrawer$OnDrawerScrollListener", "android.widget.TabHost$OnTabChangeListener",
			"android.widget.TextView$OnEditorActionListener", "android.widget.TimePicker$OnTimeChangedListener",
			"android.widget.ZoomButtonsController$OnZoomListener", };

	/**
	 * setContentView : Activity.setContentView(...) dialog.setContentView(...)
	 * inflate: View.inflate(...), LayoutInflater.inflate(...)
	 */
	private static String[] LAYOUT_RELATIVE_METHOD = new String[] { "setContentView", "inflate" };

	private static Set<SootClass> sCallBackSootClasses = null;

	public static Set<SootClass> getCallBackSootClasses() {
		if (sCallBackSootClasses == null) {
			sCallBackSootClasses = new HashSet<>();
			for (String className : ANDROID_CALLBACKS) {
				SootClass sootClass = Scene.v().getSootClass(className);
				sCallBackSootClasses.add(sootClass);
			}
		}
		return sCallBackSootClasses;
	}

	public static boolean isDefinedCallBack(SootClass sootClass) {
		for (SootClass interfaceClass : sootClass.getInterfaces()) {
			for (String className : ANDROID_CALLBACKS) {
				if (InheritanceProcess.isInheritedFromGivenClass(interfaceClass, className))
					return true;
			}
		}
		return false;
	}

	public static MultiMap<SootClass, SootMethod> getCallBackMultiMap(Set<SootClass> sootClasses) {
		Set<SootClass> allCallBackClasses = getCallBackSootClasses();
		MultiMap<SootClass, SootMethod> map = new HashMultiMap<>();
		for (SootClass referenceClass : sootClasses) {
			for (SootClass interfaceClass : referenceClass.getInterfaces()) {
				if (allCallBackClasses.contains(interfaceClass)) {
					for (SootMethod sootMethod : MethodSynchronize.getSootMethods(interfaceClass)) {
						SootMethod realInvolkedMethod = MethodUtil.getMethodBySubSignature(referenceClass,
								sootMethod.getSubSignature());
						if (realInvolkedMethod != null) {
							map.put(interfaceClass, realInvolkedMethod);
						}
					}
				}
			}
		}
		return map;
	}

	public static class ActivityInfo {
		public Set<SootClass> mInvokedSootClasses = null;
		public Set<String> mInvokedLayoutXmls = null;

		private ActivityInfo() {
		};
	}

	public static ActivityInfo getSootClassesInvoked(SootClass sootClass, Set<SootClass> visitiedClasses,
			Set<SootMethod> visitiedSootMethods) {
		ActivityInfo activityInfo = new ActivityInfo();
		Set<SootClass> sootClassesInvoked = new HashSet<>();
		activityInfo.mInvokedSootClasses = sootClassesInvoked;
		sootClassesInvoked.add(sootClass);
		if (visitiedClasses == null) {
			visitiedClasses = new HashSet<>();
		}
		if (visitiedClasses.contains(sootClass) || InheritanceProcess.isClassInSystemPackage(sootClass.getName())) {
			return activityInfo;
		}
		visitiedClasses.add(sootClass);
		List<SootMethod> sootMethods = MethodSynchronize.getSootMethods(sootClass);
		for (int i = 0; i < sootMethods.size(); i++) {
			SootMethod sootMethod = sootMethods.get(i);
			if (visitiedSootMethods == null) {
				visitiedSootMethods = new HashSet<>();
			}
			if (visitiedSootMethods.contains(sootMethod)) {
				continue;
			}
			visitiedSootMethods.add(sootMethod);
			Body body = null;
			try {
				body = sootMethod.getActiveBody();
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
//			Body body = AsyncTaskDetector.sMethodSignatureToBody.get(sootMethod
//					.getSignature());
			if (body == null) {
				continue;
			}
			for (Unit unit : body.getUnits()) {
				if (unit instanceof Stmt) {
					Stmt stmt = (Stmt) unit;
					if (stmt.containsInvokeExpr()) {
						InvokeExpr invokeExpr = stmt.getInvokeExpr();
						SootMethod invokeMethod = invokeExpr.getMethod();
						SootClass invokeClass = invokeMethod.getDeclaringClass();
						sootClassesInvoked.add(invokeClass);
						sootClassesInvoked.addAll(getSootClassesInvoked(invokeClass, visitiedClasses,
								visitiedSootMethods).mInvokedSootClasses);

						for (String layoutMethod : LAYOUT_RELATIVE_METHOD) {
							if (invokeMethod.getName().equals(layoutMethod)) {
								for (Value value : invokeExpr.getArgs()) {
									if (value instanceof IntConstant) {

										IntConstant intConstant = (IntConstant) value;
										Map<Integer, String> layoutFields = getLayoutFields();
										if (layoutFields.containsKey(intConstant.value)) {
											if (activityInfo.mInvokedLayoutXmls == null) {
												activityInfo.mInvokedLayoutXmls = new HashSet<>();
											}
											activityInfo.mInvokedLayoutXmls.add(layoutFields.get(intConstant.value));
										}
									}
								}
							}
						}

					}

					if (unit instanceof AssignStmt) {
						AssignStmt assignStmt = (AssignStmt) unit;
						if (assignStmt.getRightOp() == null) {
							continue;
						}
						Type type = assignStmt.getRightOp().getType();
						if (type instanceof RefType) {
							RefType refType = (RefType) type;
							SootClass refClass = refType.getSootClass();
							sootClassesInvoked.add(refClass);
							sootClassesInvoked.addAll(getSootClassesInvoked(refClass, visitiedClasses,
									visitiedSootMethods).mInvokedSootClasses);
						}
					}
				}
			}
		}
		return activityInfo;
	}

	private static Map<Integer, String> sLayoutFields = null;

	public synchronized static Map<Integer, String> getLayoutFields() {
		if (sLayoutFields == null) {
			sLayoutFields = new HashMap<>();
		}
		ProcessManifest manifest = null;
		try {
			manifest = new ProcessManifest(LeopardMain.getApkFullPath());
			manifest.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (manifest != null) {
			String packageName = manifest.getPackageName();
			SootClass rLayout = Scene.v().getSootClass(packageName + ".R$layout");
			for (SootField sootField : rLayout.getFields()) {
				if (sootField.getTags() != null) {
					for (Tag tag : sootField.getTags()) {
						int key = -1;
						String value = null;
						if (tag instanceof IntegerConstantValueTag) {
							key = ((IntegerConstantValueTag) tag).getIntValue();
							value = "res/layout/" + sootField.getName() + ".xml";
						}
						if (key != -1) {
							sLayoutFields.put(key, value);
							break;
						}
					}
				}
			}
		}
		return sLayoutFields;
	}

	private AndroidCallBacks() {
	}

}
