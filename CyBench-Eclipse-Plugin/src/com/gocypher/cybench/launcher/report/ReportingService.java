package com.gocypher.cybench.launcher.report;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import com.gocypher.cybench.core.utils.JSONUtils;
import org.openjdk.jmh.results.RunResult;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.gocypher.cybench.core.utils.SecurityUtils;
import com.gocypher.cybench.launcher.model.BenchmarkOverviewReport;
import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.gocypher.cybench.launcher.model.SecuredReport;
import com.gocypher.cybench.launcher.utils.ComputationUtils;
import com.gocypher.cybench.launcher.utils.SecurityBuilder;
import com.jcabi.manifests.Manifests;


public class ReportingService {
	private static ReportingService instance ;

    private ReportingService (){

    }

    public static ReportingService getInstance (){
        if (instance == null ){
            instance = new ReportingService () ;
        }
        return instance ;
    }

    public BenchmarkOverviewReport createBenchmarkReport (Collection<RunResult> jmhResults, Map<String,Map<String,String>>customBenchmarksMetadata){
        BenchmarkOverviewReport overviewReport = new BenchmarkOverviewReport() ;
        for (RunResult item :jmhResults){
            BenchmarkReport report = new BenchmarkReport() ;
            if (item.getPrimaryResult() != null) {
                report.setScore(item.getPrimaryResult().getScore());
                report.setUnits(item.getPrimaryResult().getScoreUnit());
                if (item.getPrimaryResult().getStatistics() != null){
                    report.setN(item.getPrimaryResult().getStatistics().getN());
                    report.setMeanScore(item.getPrimaryResult().getStatistics().getMean());
                    report.setMaxScore(item.getPrimaryResult().getStatistics().getMax());
                    report.setMinScore(item.getPrimaryResult().getStatistics().getMin());
                    if (!Double.isNaN(item.getPrimaryResult().getStatistics().getStandardDeviation())) {
                        report.setStdDevScore(item.getPrimaryResult().getStatistics().getStandardDeviation());
                    }
                }

            }
            if (item.getParams() != null) {
                report.setName(item.getParams().getBenchmark());
                report.setMode(item.getParams().getMode().shortLabel());
                //System.out.println("id:"+item.getParams().id());
                //System.out.println("Mode"+item.getParams().getMode().longLabel());
            }

            report.setGcCalls(getScoreFromJMHSecondaryResult(item,"�gc.count"));
            report.setGcTime(getScoreFromJMHSecondaryResult(item,"�gc.time"));
            report.setGcAllocationRate(getScoreFromJMHSecondaryResult(item,"�gc.alloc.rate"));
            report.setGcAllocationRateNorm(getScoreFromJMHSecondaryResult(item,"�gc.alloc.rate.norm"));
            report.setGcChurnPsEdenSpace(getScoreFromJMHSecondaryResult(item,"�gc.churn.PS_Eden_Space"));
            report.setGcChurnPsEdenSpaceNorm(getScoreFromJMHSecondaryResult(item,"�gc.churn.PS_Eden_Space.norm"));
            report.setGcChurnPsSurvivorSpace(getScoreFromJMHSecondaryResult(item,"�gc.churn.PS_Survivor_Space"));
            report.setGcChurnPsSurvivorSpaceNorm(getScoreFromJMHSecondaryResult(item,"�gc.churn.PS_Survivor_Space.norm"));

            report.setThreadsAliveCount(getScoreFromJMHSecondaryResult(item,"�threads.alive"));
            report.setThreadsDaemonCount(getScoreFromJMHSecondaryResult(item,"�threads.daemon"));
            report.setThreadsStartedCount(getScoreFromJMHSecondaryResult(item,"�threads.started"));

            report.setThreadsSafePointSyncTime(getScoreFromJMHSecondaryResult(item,"�rt.safepointSyncTime"));
            report.setThreadsSafePointTime(getScoreFromJMHSecondaryResult(item,"�rt.safepointTime"));
            report.setThreadsSafePointsCount(getScoreFromJMHSecondaryResult(item,"�rt.safepoints"));

            report.setThreadsSyncContendedLockAttemptsCount(getScoreFromJMHSecondaryResult(item,"�rt.sync.contendedLockAttempts"));
            report.setThreadsSyncMonitorFatMonitorsCount(getScoreFromJMHSecondaryResult(item,"�rt.sync.fatMonitors"));
            report.setThreadsSyncMonitorFutileWakeupsCount(getScoreFromJMHSecondaryResult(item,"�rt.sync.futileWakeups"));
            report.setThreadsSyncMonitorDeflations(getScoreFromJMHSecondaryResult(item,"�rt.sync.monitorDeflations"));
            report.setThreadsSyncMonitorInflations(getScoreFromJMHSecondaryResult(item,"�rt.sync.monitorInflations"));
            report.setThreadsSyncNotificationsCount(getScoreFromJMHSecondaryResult(item,"�rt.sync.notifications"));

            report.setThreadsSyncParksCount(getScoreFromJMHSecondaryResult(item,"�rt.sync.parks"));

            report.setThreadsSafePointsInterval(getScoreFromJMHSecondaryResult(item,"�safepoints.interval"));
            report.setThreadsSafePointsPause(getScoreFromJMHSecondaryResult(item,"�safepoints.pause"));
            report.setThreadsSafePointsPauseAvg(getScoreFromJMHSecondaryResult(item,"�safepoints.pause.avg"));
            report.setThreadsSafePointsPauseCount(getScoreFromJMHSecondaryResult(item,"�safepoints.pause.count"));
            report.setThreadsSafePointsPauseTTSP(getScoreFromJMHSecondaryResult(item,"�safepoints.ttsp"));
            report.setThreadsSafePointsPauseTTSPAvg(getScoreFromJMHSecondaryResult(item,"�safepoints.ttsp.avg"));
            report.setThreadsSafePointsPauseTTSPCount(getScoreFromJMHSecondaryResult(item,"�safepoints.ttsp.count"));

            /*System.out.println("Score:"+result.getPrimaryResult().getScore());
            System.out.println("Stats:"+result.getPrimaryResult().getStatistics());
            System.out.println("getBenchmarkResults:"+result.getBenchmarkResults().size());
            System.out.println("getAggregatedResult:"+result.getAggregatedResult().getBenchmarkResults());
            System.out.println("getSecondaryResults:"+result.getSecondaryResults());
            System.out.println("\n\n");
            */

            //System.out.println("Report class name:"+report.getReportClassName());
            //String metaInfoData = report.getReportClassName()
            String manifestData = null ;
                   
            if (Manifests.exists("customBenchmarkMetadata")) {
                manifestData = Manifests.read("customBenchmarkMetadata");
            }
            
            
            Map<String,Map<String,String>> benchmarksMetadata =  ComputationUtils.parseCustomBenchmarkMetadata(manifestData);
            Map<String, String> benchProps;
            if(manifestData != null) {
                benchProps = prepareBenchmarkProperties(report.getReportClassName(), benchmarksMetadata);
            } else {
                benchProps = prepareBenchmarkProperties(report.getReportClassName(), customBenchmarksMetadata);
            }
            if(benchProps.get("benchCategory") != null) {
//                LOG.info("benchCategory {}", benchProps.get("benchCategory"));
            report.setCategory(benchProps.get("benchCategory"));
            }
            if(benchProps.get("benchContext") != null) {
//                LOG.info("benchContext {}", benchProps.get("benchContext"));
            report.setContext(benchProps.get("benchContext"));
            }
            if(benchProps.get("benchVersion") != null) {
//                LOG.info("benchVersion {}", benchProps.get("benchVersion"));
            report.setVersion(benchProps.get("benchVersion"));
            }
            report.recalculateScoresToMatchNewUnits();
//            LOG.info("RAPORT {}", report);
            overviewReport.addToBenchmarks(report);
        }

        overviewReport.setTimestamp(System.currentTimeMillis());
        overviewReport.setTimestampUTC(ZonedDateTime.now( ZoneOffset.UTC ).toInstant().toEpochMilli());
        overviewReport.computeScores();

        return overviewReport ;
    }
    public Map<String, String> prepareBenchmarkProperties(String className, Map<String,Map<String,String>>customBenchmarksMetadata){
        Map<String, String> benchmarkProperties = new HashMap<>();
        try {
            if (customBenchmarksMetadata.get(className) != null) {
                if (customBenchmarksMetadata.get(className).get("category") != null) {
                    benchmarkProperties.put("benchCategory", customBenchmarksMetadata.get(className).get("category"));
                }
                if (customBenchmarksMetadata.get(className).get("context") != null) {
                    benchmarkProperties.put("benchContext", customBenchmarksMetadata.get(className).get("context"));
                }
                if (customBenchmarksMetadata.get(className).get("version") != null) {
                    benchmarkProperties.put("benchVersion", customBenchmarksMetadata.get(className).get("version"));
                }
            }else{
                benchmarkProperties.put("benchCategory", "CUSTOM");
                benchmarkProperties.put("benchContext", "Custom");
                benchmarkProperties.put("benchVersion", "None");
            }
            return benchmarkProperties;
        } catch (Exception e) {
        	System.out.println("Error on resolving benchmarks category, context and version: class={}" +className);
        	e.printStackTrace();
        }
        return benchmarkProperties;
    }
    public Map<String, String> prepareBenchmarkSettings(String className, Map<String,Map<String,String>>customBenchmarksMetadata){
        Map<String, String> benchmarkProperties = new HashMap<>();
        try {
            if (customBenchmarksMetadata.get(className) != null) {
                if (customBenchmarksMetadata.get(className).get("context") != null) {
                    benchmarkProperties.put("benchContext", customBenchmarksMetadata.get(className).get("context"));
                }
                if (customBenchmarksMetadata.get(className).get("version") != null) {
                    benchmarkProperties.put("benchVersion", customBenchmarksMetadata.get(className).get("version"));
                }
            }else{
                benchmarkProperties.put("benchContext", "Custom");
                benchmarkProperties.put("benchVersion", "None");
            }
            return benchmarkProperties;
        } catch (Exception e) {
            benchmarkProperties.put("benchContext", "Custom");
            benchmarkProperties.put("benchVersion", "None");
            System.out.println("Error on resolving category: class={}"+ className);
            e.printStackTrace();
        }
        return benchmarkProperties;
    }

    protected String getVersion(String fullClassName) {
        try {
            Class<?> clazz = Class.forName(fullClassName);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Manifest manifest = new Manifest(loader.getResourceAsStream("META-INF/MANIFEST.MF"));
            String benchmarkPackageString = clazz.getPackage().getName().replace(".", "-")+"-version";
            return manifest.getMainAttributes().getValue(benchmarkPackageString);
        } catch (Exception e) {
        	System.out.println("Could not locate the benchmark version"+e);
        }
        return null;
    }

    public String prepareReportForDelivery(SecurityBuilder securityBuilder, BenchmarkOverviewReport report) {
        try {
            System.out.println("Preparing report: encrypt and sign...");
            SecuredReport securedReport = createSecuredReport(report);
            securityBuilder.generateSecurityHashForReport(securedReport.getReport());
            securedReport.setSignatures(securityBuilder.buildSignatures());
            String plainReport = JSONUtils.marshalToJson(securedReport);
            return SecurityUtils.encryptReport(plainReport);
        } finally {
        	System.out.println("Report prepared: encrypted and signed");
        }
    }

    private SecuredReport createSecuredReport (BenchmarkOverviewReport report){
        SecuredReport securedReport = new SecuredReport() ;
        securedReport.setReport(JSONUtils.marshalToJson(report));
        return securedReport ;
    }

    private Double getScoreFromJMHSecondaryResult (RunResult result,String key){
        if (result != null && result.getSecondaryResults() != null){
            if (result.getSecondaryResults().get(key)!= null){
                return result.getSecondaryResults().get(key).getScore() ;
            }
        }
        return null ;
    }
}
