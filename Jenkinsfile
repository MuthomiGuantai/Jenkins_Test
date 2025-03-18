pipeline {
    agent any
    tools {
        maven 'Maven3' // Ensure this matches the name in Jenkins config
    }
    environment {
        // Define ports for verification
        SERVICE_REGISTRY_PORT = '8761'
        CONFIG_SERVER_PORT = '8104'
        API_GATEWAY_PORT = '8100'
        MEDICAL_SERVICE_PORT = '8101'
        PATIENT_SERVICE_PORT = '8102'
        DEPARTMENT_SERVICE_PORT = '8103'
    }
    stages {
        stage('Checkout') {
            steps {
                // Clone the repository
                git branch: 'main', url: 'https://github.com/MuthomiGuantai/Jenkins_Test'
            }
        }
        stage('Build All') {
            parallel {
                stage('Build Service Registry') {
                    steps {
                        dir('Service_Registry') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Config Server') {
                    steps {
                        dir('Config_Server') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build API Gateway') {
                    steps {
                        dir('Api_Gateway') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Medical Service') {
                    steps {
                        dir('Medical_Service') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Patient Service') {
                    steps {
                        dir('Patient_Service') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Department Service') {
                    steps {
                        dir('Department_Service') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }
        stage('Setup Databases') {
            steps {
                // Start MySQL container for Medical_Service
                sh 'docker run -d --name mysql-medical -p 3306:3306 -e MYSQL_ROOT_PASSWORD=${Clearme@1824} -e MYSQL_DATABASE=Medical mysql:latest'
                // Start MySQL container for Patient_Service on a different port
                sh 'docker run -d --name mysql-patient -p 3307:3306 -e MYSQL_ROOT_PASSWORD=${Clearme@1824} -e MYSQL_DATABASE=Patient_db mysql:latest'
                // Start MySQL container for Department_Service on a different port
                sh 'docker run -d --name mysql-department -p 3308:3306 -e MYSQL_ROOT_PASSWORD=${Clearme@1824} -e MYSQL_DATABASE=Department_db mysql:latest'
                // Wait for all databases to be ready
                sleep 30
            }
        }
        stage('Deploy Dependencies') {
            steps {
                // Deploy Service Registry (Eureka) first
                dir('Service_Registry') {
                    sh 'java -jar target/Service_Registry-0.0.1-SNAPSHOT.jar &'
                    sleep 60 // Wait for Eureka to start
                }
                // Deploy Config Server next
                dir('Config_Server') {
                    sh 'java -jar target/Config_Server-0.0.1-SNAPSHOT.jar &'
                    sleep 60 // Wait for Config Server to start
                }
            }
        }
        stage('Deploy Services') {
            parallel {
                stage('Deploy API Gateway') {
                    steps {
                        dir('Api_Gateway') {
                            sh 'java -jar target/Api_Gateway-0.0.1-SNAPSHOT.jar &'
                            sleep 60
                        }
                    }
                }
                stage('Deploy Medical Service') {
                    steps {
                        dir('Medical_Service') {
                            sh 'java -jar target/Medical_Service-0.0.1-SNAPSHOT.jar &'
                            sleep 60
                        }
                    }
                }
                stage('Deploy Patient Service') {
                    steps {
                        dir('Patient_Service') {
                            sh 'java -jar target/Patient_Service-0.0.1-SNAPSHOT.jar &'
                            sleep 60
                        }
                    }
                }
                stage('Deploy Department Service') {
                    steps {
                        dir('Department_Service') {
                            sh 'java -jar target/Department_Service-0.0.1-SNAPSHOT.jar &'
                            sleep 60
                        }
                    }
                }
            }
        }
        stage('Verify Deployment') {
            steps {
                // Check Eureka Server
                sh 'curl -f http://localhost:${SERVICE_REGISTRY_PORT} || exit 1'
                // Check Config Server
                sh 'curl -f http://localhost:${CONFIG_SERVER_PORT}/medical-service/default || exit 1'
                // Check API Gateway endpoints
                sh 'curl -f -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9BRE1JTiIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzQyMjk4ODcxLCJleHAiOjE3NDMxNjI4NzF9.xg9picu1mzvwHTTQJf2IdWQ1tTg9ZNeQctyUKC1pjPc" http://localhost:${API_GATEWAY_PORT}/medical/doctors || exit 1'
                sh 'curl -f -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9BRE1JTiIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzQyMjk4ODcxLCJleHAiOjE3NDMxNjI4NzF9.xg9picu1mzvwHTTQJf2IdWQ1tTg9ZNeQctyUKC1pjPc" http://localhost:${API_GATEWAY_PORT}/patient/medical-records || exit 1'
                sh 'curl -f -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9BRE1JTiIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzQyMjk4ODcxLCJleHAiOjE3NDMxNjI4NzF9.xg9picu1mzvwHTTQJf2IdWQ1tTg9ZNeQctyUKC1pjPc" http://localhost:${API_GATEWAY_PORT}/department/departments || exit 1'
            
            }
        }
    }
    post {
        success {
            echo 'Hospital Management System deployed successfully!'
        }
        failure {
            echo 'Deployment failed. Check logs for details.'
        }
        always {
            // Cleanup (optional): Kill background processes
            sh 'pkill -f "java -jar" || true'
        }
    }
}