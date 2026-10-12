pipeline {
    agent any

    environment {
        JAVA_HOME = '/Library/Java/JavaVirtualMachines/zulu-24.jdk/Contents/Home'
        PATH = "${JAVA_HOME}/bin:/usr/local/bin:/opt/homebrew/bin:${env.PATH}"
    }

    stages {

        stage('Environment') {
            steps {
                sh '''
                    java -version
                    mvn -version
                    docker --version
                    docker compose version
                '''
            }
        }

        stage('Test') {
            steps {
                sh 'mvn clean test -Dspring.profiles.active=test'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker compose up -d --build
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    docker compose ps
                    docker compose logs --tail=50 mindcare-diary
                '''
            }
        }
    }

    post {
        success {
            echo 'MindCare Diary deployed successfully!'
        }

        failure {
            echo 'MindCare Diary deployment failed!'
            sh 'docker compose logs --tail=100 || true'
        }
    }
}