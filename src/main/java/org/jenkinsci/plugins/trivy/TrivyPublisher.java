package org.jenkinsci.plugins.trivy;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import net.sf.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrivyPublisher extends Recorder implements SimpleBuildStep {
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
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        listener.getLogger().println("[Trivy] Processing report...");

        if (workspace == null) {
            listener.error("No workspace found");
            return;
        }

        FilePath reportFile = workspace.child(reportPath);
        if (!reportFile.exists()) {
            listener.error("Trivy report not found: " + reportFile.getRemote());
            return;
        }

        File localReport = new File(reportFile.getRemote());
        List<TrivyVulnerability> vulns = TrivyReportParser.parse(localReport);
        listener.getLogger().println("[Trivy] Found vulnerabilities: " + vulns.size());

        run.addAction(new TrivyBuildAction(vulns));
    }

    @Symbol("trivyReport")  // This makes it usable in the Pipeline DSL
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            super(TrivyPublisher.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Publish Trivy Security Report";
        }

        @Override
        public Publisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return req.bindJSON(TrivyPublisher.class, formData);
        }
    }
}
