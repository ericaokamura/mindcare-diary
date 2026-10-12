pipeline {
    agent any

    environment {
        JAVA_HOME = '/Library/Java/JavaVirtualMachines/zulu-24.jdk/Contents/Home'
        PATH = "${JAVA_HOME}/bin:/opt/homebrew/bin:${env.PATH}"
        IMAGE_NAME = 'mindcare-diary'
        CONTAINER_NAME = 'mindcare-diary'
    }

    stages {

        stage('Environment') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
                sh 'docker --version'
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

        stage('Docker Build') {
            steps {
                sh '/usr/local/bin/docker build -t mindcare-diary:$BUILD_NUMBER .'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker stop $CONTAINER_NAME || true
                    docker rm $CONTAINER_NAME || true

                    docker run -d \
                        --name $CONTAINER_NAME \
                        -p 8080:8080 \
                        $IMAGE_NAME:$BUILD_NUMBER
                '''
            }
        }
    }

    post {
        success {
            echo 'MindCare Diary deployed successfully!'
        }

        failure {
            echo 'MindCare Diary deployment failed.'
        }
    }
}