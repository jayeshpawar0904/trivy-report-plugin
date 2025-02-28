package org.jenkinsci.plugins.trivy;

import hudson.model.Run;
import jenkins.model.RunAction2;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrivyBuildAction implements RunAction2 {
    private transient Run<?, ?> run;
    private List<TrivyVulnerability> vulnerabilities;

    public TrivyBuildAction(List<TrivyVulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    @Override public String getIconFileName() { return "document.png"; }
    @Override public String getDisplayName() { return "Trivy Security Report"; }
    @Override public String getUrlName() { return "trivy-report"; }

    public List<TrivyVulnerability> getVulnerabilities() { return vulnerabilities; }

    public List<TrivyVulnerability> getSortedVulnerabilities() {
        return vulnerabilities.stream()
            .sorted(Comparator.comparing(TrivyVulnerability::getSeverity).reversed()) // Critical -> High -> Medium -> Low
            .collect(Collectors.toList());
    }
        
    public Map<String, Long> getSeverityCounts() {
        return vulnerabilities.stream()
            .collect(Collectors.groupingBy(
                TrivyVulnerability::getSeverity,
                Collectors.counting()
            ));
    }

    @Override public void onAttached(Run<?, ?> r) { this.run = r; }
    @Override public void onLoad(Run<?, ?> r) { this.run = r; }
    public Run<?, ?> getRun() { return run; }
}