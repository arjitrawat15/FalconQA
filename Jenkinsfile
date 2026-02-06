pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8'
        jdk 'JDK 11'
    }
    
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Select browser')
        choice(name: 'SUITE', choices: ['testng.xml', 'smoke-tests.xml', 'data-driven-tests.xml'], description: 'Select test suite')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run in headless mode')
    }
    
    environment {
        MAVEN_OPTS = '-Xmx2048m -Xms512m'
        TIMESTAMP = sh(script: "date +%Y%m%d_%H%M%S", returnStdout: true).trim()
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out code from repository...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Building the project...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Run Tests') {
            steps {
                echo "🧪 Running tests with ${params.BROWSER} browser..."
                script {
                    def testResult = sh(
                        script: """
                            mvn clean test \
                            -Dbrowser=${params.BROWSER} \
                            -Dheadless=${params.HEADLESS} \
                            -DsuiteXmlFile=src/test/resources/${params.SUITE}
                        """,
                        returnStatus: true
                    )
                    
                    // Store test result but don't fail build yet
                    env.TEST_EXIT_CODE = testResult.toString()
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                echo '📊 Generating test reports...'
                script {
                    // Archive Extent Reports
                    if (fileExists('test-output/reports')) {
                        archiveArtifacts artifacts: 'test-output/reports/**/*.html', allowEmptyArchive: true
                        archiveArtifacts artifacts: 'test-output/reports/**/*.csv', allowEmptyArchive: true
                    }
                    
                    // Archive Screenshots
                    if (fileExists('test-output/screenshots')) {
                        archiveArtifacts artifacts: 'test-output/screenshots/**/*.png', allowEmptyArchive: true
                    }
                    
                    // Archive Logs
                    if (fileExists('test-output/logs')) {
                        archiveArtifacts artifacts: 'test-output/logs/**/*.log', allowEmptyArchive: true
                    }
                }
            }
        }
        
        stage('Publish Results') {
            steps {
                echo '📤 Publishing test results...'
                
                // Publish TestNG results
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output/reports',
                    reportFiles: 'ExtentReport_*.html',
                    reportName: 'Extent Test Report',
                    reportTitles: 'FalconQA Test Execution Report'
                ])
                
                // Publish Performance Report
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output/reports',
                    reportFiles: 'performance-report*.txt',
                    reportName: 'Performance Report',
                    reportTitles: 'Test Performance Analysis'
                ])
            }
        }
        
        stage('Notify') {
            steps {
                script {
                    def status = env.TEST_EXIT_CODE == '0' ? 'SUCCESS ✅' : 'FAILURE ❌'
                    def color = env.TEST_EXIT_CODE == '0' ? 'good' : 'danger'
                    
                    echo "Build Status: ${status}"
                    
                    // Slack notification (optional - configure in Jenkins)
                    // slackSend(
                    //     color: color,
                    //     message: """
                    //         FalconQA Test Execution ${status}
                    //         Browser: ${params.BROWSER}
                    //         Suite: ${params.SUITE}
                    //         Build: ${env.BUILD_NUMBER}
                    //         Report: ${env.BUILD_URL}Extent_Test_Report/
                    //     """
                    // )
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up workspace...'
            cleanWs()
        }
        
        success {
            echo '✅ Pipeline completed successfully!'
        }
        
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
