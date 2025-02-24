# Trivy Report Plugin for Jenkins

## Overview
The **Trivy Report Plugin** for Jenkins is designed to analyze security vulnerabilities in container images, filesystems, and Git repositories by integrating **Trivy** scans into Jenkins pipelines. This plugin parses Trivy reports and displays vulnerability severity and package types directly in the Jenkins UI.

## What is Trivy?
[Trivy](https://github.com/aquasecurity/trivy) is an open-source vulnerability scanner for container images, filesystems, and repositories. It quickly identifies security risks, misconfigurations, and exposed secrets.

### Why Use Trivy?
- **Fast and lightweight**: Scans run efficiently without excessive resource consumption.
- **Comprehensive scanning**: Supports OS packages, application dependencies, and IaC misconfigurations.
- **Integration-friendly**: Easily integrates into CI/CD pipelines.

## Plugin Features
- Parses **Trivy JSON reports**.
- Displays vulnerabilities within Jenkins UI.
- Supports different severity levels and package types.
- Can be used as a pipeline step in Jenkinsfile.

## How to Build the Plugin
### Prerequisites
Ensure you have the following installed:
- **Java 8+**
- **Maven**
- **Jenkins Plugin Development Tools**

### Build Steps
1. Clone the repository:
   ```sh
   git clone git@github.com:jayeshpawar0904/trivy-report-plugin.git
   cd trivy-report-plugin
   ```
2. Build the plugin:
   ```sh
   mvn clean package
   ```
3. Find the `.hpi` file in the `target/` directory.

## How to Install the Plugin in Jenkins
1. Go to **Manage Jenkins** > **Manage Plugins**.
2. Click on **Advanced** > **Upload Plugin**.
3. Upload the `.hpi` file generated from the build.
4. Restart Jenkins.

## How to Use the Plugin in a Jenkins Pipeline
### Example Jenkinsfile
```groovy
pipeline {
    agent any
    stages {
        stage('Security Scan') {
            steps {
                sh 'trivy image my-container-image > trivy-report.json'
                trivyReport(reportPath: 'trivy-report.json')
            }
        }
    }
}
```

## Benefits of Using This Plugin
- **Automated security scans in CI/CD**: Ensures vulnerabilities are detected early.
- **Improved visibility**: Displays scan results within Jenkins UI.
- **Customizable reports**: Integrates with existing security workflows.

## License
This project is licensed under the **Apache 2.0 License**.

## Contributions
Feel free to fork and contribute by submitting pull requests!

## Contact
For any issues, open an [issue](https://github.com/jayeshpawar0904/trivy-report-plugin/issues) on GitHub.

