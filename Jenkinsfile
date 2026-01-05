pipeline {
    agent any
    
    tools {
        maven 'Maven-3'
        jdk 'JDK-21'
    }
    
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Select browser')
        choice(name: 'ENVIRONMENT', choices: ['demo', 'staging', 'production'], description: 'Select environment')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from repository...'
                checkout scm
            }
        }
        
        stage('Clean') {
            steps {
                echo 'Cleaning previous build artifacts...'
                bat 'mvn clean'
            }
        }
        
        stage('Compile') {
            steps {
                echo 'Compiling the project...'
                bat 'mvn compile'
            }
        }
        
        stage('Run Tests') {
            steps {
                echo "Running tests on ${params.BROWSER} browser..."
                bat """
                    mvn test -Dbrowser=${params.BROWSER} -Dallure.url=${BUILD_URL}allure/
                """
            }
        }
        
        stage('Generate Allure Report') {
            steps {
                echo 'Generating Allure Report...'
                script {
                    allure includeProperties: false,
                           jdk: '',
                           results: [[path: 'target/allure-results']]
                }
            }
        }
        
        stage('Archive Results') {
            steps {
                echo 'Archiving test results...'
                archiveArtifacts artifacts: '**/target/surefire-reports/*.xml', allowEmptyArchive: true
                archiveArtifacts artifacts: '**/screenshots/*.png', allowEmptyArchive: true
                archiveArtifacts artifacts: '**/logs/*.log', allowEmptyArchive: true
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        
        success {
            echo '✓ Build Successful!'
            emailext(
                subject: "✓ OpenEMR Tests - Build #${BUILD_NUMBER} - SUCCESS",
                body: """
                    <html>
                    <body>
                        <h2 style="color: green;">Build Successful ✓</h2>
                        <p><b>Project:</b> ${JOB_NAME}</p>
                        <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                        <p><b>Browser:</b> ${params.BROWSER}</p>
                        <p><b>Environment:</b> ${params.ENVIRONMENT}</p>
                        <p><b>Build URL:</b> <a href="${BUILD_URL}">${BUILD_URL}</a></p>
                        <p><b>Allure Report:</b> <a href="${BUILD_URL}allure/">View Report</a></p>
                    </body>
                    </html>
                """,
                to: 'your-email@example.com',
                mimeType: 'text/html'
            )
        }
        
        failure {
            echo '✗ Build Failed!'
            emailext(
                subject: "✗ OpenEMR Tests - Build #${BUILD_NUMBER} - FAILURE",
                body: """
                    <html>
                    <body>
                        <h2 style="color: red;">Build Failed ✗</h2>
                        <p><b>Project:</b> ${JOB_NAME}</p>
                        <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                        <p><b>Browser:</b> ${params.BROWSER}</p>
                        <p><b>Environment:</b> ${params.ENVIRONMENT}</p>
                        <p><b>Build URL:</b> <a href="${BUILD_URL}">${BUILD_URL}</a></p>
                        <p><b>Console Output:</b> <a href="${BUILD_URL}console">View Console</a></p>
                        <p>Please check the logs and screenshots for more details.</p>
                    </body>
                    </html>
                """,
                to: 'your-email@example.com',
                mimeType: 'text/html',
                attachLog: true
            )
        }
    }
}
