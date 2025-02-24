package org.jenkinsci.plugins.trivy;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrivyPublisher extends Recorder {
    private String reportPath;

    @DataBoundConstructor
    public TrivyPublisher() {
        this.reportPath = "trivy-report.json";
    }

    public String getReportPath() {
        return reportPath;
    }

    @DataBoundSetter
    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        try {
            FilePath workspace = build.getWorkspace();
            if (workspace == null) {
                listener.error("No workspace found");
                return false;
            }

            FilePath reportFile = workspace.child(reportPath);
            listener.getLogger().println("[Trivy] Scanning report at: " + reportFile.getRemote());

            if (!reportFile.exists()) {
                listener.error("Trivy report not found at: " + reportFile.getRemote());
                return false;
            }

            File localReport = new File(reportFile.getRemote());
            List<TrivyVulnerability> vulns = TrivyReportParser.parse(localReport);
            listener.getLogger().println("[Trivy] Found vulnerabilities: " + vulns.size());
            
            build.addAction(new TrivyBuildAction(vulns));
            return true;
        } catch (Exception e) {
            listener.error("Trivy report processing failed: " + e.getMessage());
            return false;
        }
    }

    @Symbol("trivyReport")
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Publish Trivy Security Report";
        }
    }
}