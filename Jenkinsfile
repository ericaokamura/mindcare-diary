pipeline {
    agent any

    environment {
        JAVA_HOME = '/Library/Java/JavaVirtualMachines/zulu-24.jdk/Contents/Home'
        PATH = "${JAVA_HOME}/bin:/usr/local/bin:/opt/homebrew/bin:${env.PATH}"

        IMAGE_NAME = 'mindcare-diary'
        CONTAINER_NAME = 'mindcare-diary'

        DB_PASSWORD = credentials('mindcare_db_password')
        OPEN_AI_API_KEY = credentials('openai_api_key')
        MINDCARE_DATA_KEY = credentials('mindcare_crypto_key')
    }

    stages {

        stage('Environment') {
            steps {
                sh '''
                    echo "JAVA_HOME=$JAVA_HOME"
                    echo "PATH=$PATH"

                    java -version
                    mvn -version

                    which docker
                    docker --version
                    docker info
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

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$BUILD_NUMBER .'
            }
        }

        stage('Deploy') {

            when {
                    branch 'feature/oracle-db'
            }

            steps {
                sh '''
                    echo "Stopping previous container..."

                    docker stop $CONTAINER_NAME || true
                    docker rm $CONTAINER_NAME || true

                    echo "Starting version $BUILD_NUMBER..."

                    docker run -d \
                        --name $CONTAINER_NAME \
                        --network mindcare-network \
                        -p 8080:8080 \
                        -e DB_PASSWORD="$DB_PASSWORD" \
                        -e OPEN_AI_API_KEY="$OPEN_AI_API_KEY" \
                        -e MINDCARE_DATA_KEY="$MINDCARE_DATA_KEY" \
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