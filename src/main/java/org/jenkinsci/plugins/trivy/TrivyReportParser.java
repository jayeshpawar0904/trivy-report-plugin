package org.jenkinsci.plugins.trivy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrivyReportParser {
    public static List<TrivyVulnerability> parse(File reportFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(reportFile);
        List<TrivyVulnerability> vulns = new ArrayList<>();

        JsonNode results = root.path("Results");
        if (results.isMissingNode() || !results.isArray()) return vulns;

        for (JsonNode result : results) {
            JsonNode vulnerabilities = result.path("Vulnerabilities");
            if (vulnerabilities == null || !vulnerabilities.isArray()) continue;

            for (JsonNode vuln : vulnerabilities) {
                TrivyVulnerability tv = new TrivyVulnerability();
                tv.setVulnerabilityID(getText(vuln, "VulnerabilityID"));
                tv.setSeverity(getText(vuln, "Severity"));
                tv.setPkgName(getText(vuln, "PkgName"));
                tv.setDescription(getText(vuln, "Description"));
                tv.setPrimaryUrl(getText(vuln, "PrimaryURL"));
                vulns.add(tv);
            }
        }
        return vulns;
    }

    private static String getText(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : "N/A";
    }
}