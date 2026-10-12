pipeline {
    agent any

    environment {
        JAVA_HOME = '/Library/Java/JavaVirtualMachines/zulu-24.jdk/Contents/Home'
        PATH = "${JAVA_HOME}/bin:/usr/local/bin:/opt/homebrew/bin:${env.PATH}"

        IMAGE_NAME = 'mindcare-diary'
        CONTAINER_NAME = 'mindcare-api'

        DB_PASSWORD = credentials('mindcare_db_password')
        OPEN_AI_API_KEY = credentials('openai_api_key')
        MINDCARE_DATA_KEY = credentials('mindcare_crypto_key')
    }

    stages {

        stage('Environment') {
            steps {
                sh '''
                    echo "JAVA_HOME=$JAVA_HOME"

                    java -version
                    mvn -version

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
                sh '''
                    docker build \
                        -t "$IMAGE_NAME:$BUILD_NUMBER" \
                        .
                '''
            }
        }

        stage('Deploy') {
            when {
                branch 'feature/oracle-db'
            }

            steps {
                sh '''
                    echo "Removing previous Spring Boot container..."

                    docker stop "$CONTAINER_NAME" || true
                    docker rm "$CONTAINER_NAME" || true

                    echo "Starting MindCare API build $BUILD_NUMBER..."

                    docker run -d \
                        --name "$CONTAINER_NAME" \
                        --network mindcare-network \
                        --restart unless-stopped \
                        -p 8080:8080 \
                        -e DB_PASSWORD \
                        -e OPEN_AI_API_KEY \
                        -e MINDCARE_DATA_KEY \
                        "$IMAGE_NAME:$BUILD_NUMBER"

                    echo "Waiting for Spring Boot..."
                    sleep 15

                    echo "Container status:"
                    docker ps -a --filter "name=$CONTAINER_NAME"

                    if ! docker ps \
                        --filter "name=^/${CONTAINER_NAME}$" \
                        --filter "status=running" \
                        --format '{{.Names}}' \
                        | grep -qx "$CONTAINER_NAME"; then

                        echo "MindCare API failed to start."
                        docker logs "$CONTAINER_NAME"
                        exit 1
                    fi

                    echo "MindCare API is running."
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