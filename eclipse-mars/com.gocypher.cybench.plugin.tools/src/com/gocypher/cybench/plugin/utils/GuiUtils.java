package com.gocypher.cybench.plugin.utils;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.gocypher.cybench.plugin.Activator;
import com.gocypher.cybench.plugin.model.ICybenchPartView;
import com.gocypher.cybench.plugin.views.CyBenchExplorerView;
import com.gocypher.cybench.plugin.views.ReportsDisplayView;

public class GuiUtils {
	public static ILog LOG = Activator.getDefault().getLog();
	private static ResourceBundle titles = ResourceBundle.getBundle("titles");
	
	public static String getKeyName(String key) {
		if (titles.containsKey(key)) {
			return titles.getString(key);
	    } 
		return key;
	}
	public static String convertNumToStringByLength(String value) {
        try {


            double v = Double.parseDouble(value);
            if (value != null) {
                if (value.indexOf(".") < 1) {
                    return value;
                }
                if (Math.abs(v) > 1) {
                    return convertNumToStringFrac(v, 2, 2);
                }
                if (Math.abs(v) > 0.1) {
                    return convertNumToStringFrac(v, 2, 2);
                }
                if (Math.abs(v) > 0.01) {
                    return convertNumToStringFrac(v, 3, 3);
                }
                if (Math.abs(v) > 0.001) {
                    return convertNumToStringFrac(v, 4, 4);
                }
                if (Math.abs(v) > 0.0001) {
                    return convertNumToStringFrac(v, 5, 5);
                }
                if (Math.abs(v) > 0.00001) {
                    return convertNumToStringFrac(v, 6, 6);
                }
                if (v == 0) {
                    return convertNumToStringFrac(v, 0, 0);
                }
                return convertNumToStringFrac(v, 6, 8);
            }
        } catch (NumberFormatException e) {
            return value;
        }


        return value;
    }

    private static String convertNumToStringFrac(Object value, int minFractionDigits, int maxFractionDigits) {

        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        decimalFormat.setMinimumFractionDigits(minFractionDigits);
        decimalFormat.setMinimumFractionDigits(maxFractionDigits);
        decimalFormat.setDecimalFormatSymbols(symbols);
        return decimalFormat.format(value);
    }
    public static String encodeBase64 (String plainString) {
    	if (plainString != null) {
    		return new String (Base64.getEncoder().encode(plainString.getBytes())) ;
    	}
    	return null ;
    }
    public static String decodeBase64 (String base64String) {
    	if (base64String != null) {
    		return new String (Base64.getDecoder().decode(base64String.getBytes())) ;
    	}
    	return null ;
    }
    
    public static void refreshCybenchExplorer () {
    	Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	try {		    	
		    		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();		    	
					IViewPart view = page.findView(CyBenchExplorerView.ID) ;
		    		if (view instanceof ICybenchPartView) {
		    			((ICybenchPartView)view).refreshView();
		    		}
//			    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ReportsDisplayView.ID) ; 
		    	}catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		});	
    }
    public static void openReportDisplayView (String fullPathToReport) {
    	if (fullPathToReport != null && !fullPathToReport.isEmpty()) {
	    	Display.getDefault().asyncExec(new Runnable() {
			    public void run() {
			    	try {
			    		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			    		String reportIdentifier = encodeBase64(fullPathToReport) ;
			    		page.showView(ReportsDisplayView.ID,reportIdentifier , IWorkbenchPage.VIEW_ACTIVATE);			
		    		
	//			    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ReportsDisplayView.ID) ; 
			    	}catch (Exception e) {
			    		e.printStackTrace();
			    	}
			    }
			});	
    	}
    	else {
    		GuiUtils.logError("Error: full path to report is null or empty, view can't be opened!");
    	}
    }
    
    public static void logMessage (String message) {
    	MessageConsole cyBenchConsole = LauncherUtils.findConsole("CyBench Console");
		cyBenchConsole.clearConsole();
		cyBenchConsole.activate();
		MessageConsoleStream out = cyBenchConsole.newMessageStream();
		out.println(message);
		
    }
	
    public static ImageDescriptor getCustomImage (String pathToImage) {
    	URL url = FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID), new Path(pathToImage), null);
    	return ImageDescriptor.createFromURL(url);
    }
    public static void attachAptBasedPreferences (IJavaProject javaProject) throws Exception {
		ProjectScope projectScope = new ProjectScope(javaProject.getProject()) ;
		IEclipsePreferences prefs = projectScope.getNode("org.eclipse.jdt.apt.core") ;
		prefs.putBoolean("org.eclipse.jdt.apt.aptEnabled", true);
		prefs.putBoolean("org.eclipse.jdt.apt.reconcileEnabled", true);
		
		prefs.put("org.eclipse.jdt.apt.genSrcDir", "target/jmh-generated");
		prefs.put("org.eclipse.jdt.apt.genTestSrcDir", "target/jmh-generated-tests");
		prefs.flush();
		
	}
    public static void refreshProject (IJavaProject javaProject) {
		if (javaProject != null) {
			try {
				javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}catch (Exception e) {
				GuiUtils.logError("Error on proejct refresh:",e);
			}
		}
		else {
			GuiUtils.logError("Unable to refresh null project!");
		}
		
	}
    public static IJavaProject resolveJavaProject (ISelection selection) {
		IJavaProject javaProject = null ;
		if (selection instanceof IStructuredSelection) {
    		IStructuredSelection ss = (IStructuredSelection) selection;
    		GuiUtils.logInfo(ss.getFirstElement().toString());
    		for (Object elem : ss.toList()) {
    			if (elem instanceof IProject) {
    				javaProject = (IJavaProject)JavaCore.create((IProject)elem);
    			}
    		}
		}
		return javaProject;
	}
    public static void logInfo (String message) {
    	if (LOG != null) {
    		LOG.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, message));
    	}    	
    }
    public static void logError (String message, Throwable throwable) {
    	if (LOG != null) {
    		LOG.log(new Status(IStatus.ERROR,Activator.PLUGIN_ID,message,throwable));
    	}
    }
    public static void logError (String message) {
    	if (LOG != null) {
    		LOG.log(new Status(IStatus.ERROR,message,""));
    	}
    }
}
