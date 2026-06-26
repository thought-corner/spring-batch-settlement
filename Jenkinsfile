// settlement-batch CI 파이프라인.
// Jenkins(컨테이너)가 GitHub에서 소스를 받아 워크스페이스 안에서 직접 빌드한 뒤 배치를 실행한다.
// - 빌드 JDK: 컨테이너엔 JDK21만 있으므로, settings.gradle의 foojay 리졸버가 JDK17을 자동 조달한다.
// - DB 접속: 컨테이너의 localhost는 컨테이너 자신이므로, 호스트의 MySQL은 host.docker.internal 로 접근한다.
pipeline {
    agent any

    environment {
        JAR = 'build/libs/settlement-batch-0.0.1-SNAPSHOT.jar'
        // 컨테이너 → 호스트(Mac)의 MySQL 3306. application.yaml의 localhost 설정을 실행 시 덮어쓴다.
        DB_URL = 'jdbc:mysql://host.docker.internal:3306/settlement?serverTimezone=Asia/Seoul&characterEncoding=UTF-8'
    }

    stages {
        stage('Build') {
            steps {
                // 테스트는 DB가 필요해 일단 제외(-x test). 추후 H2/Testcontainers로 별도 구성 예정.
                sh 'chmod +x ./gradlew'
                sh './gradlew clean bootJar -x test'
            }
        }

        stage('Run Batch') {
            steps {
                script {
                    // 정산 대상 = 7일 전. time 파라미터는 매 빌드마다 달라 'JobInstance 중복'을 피한다.
                    def targetDate = sh(script: "date -d '7 days ago' +%F", returnStdout: true).trim()
                    echo "정산 대상 날짜: ${targetDate}"
                    sh """
                        java -jar ${JAR} \
                          --spring.batch.job.enabled=true \
                          --spring.batch.job.name=settlementJob \
                          --spring.datasource.url='${DB_URL}' \
                          settlementDate=${targetDate} \
                          time=\$(date +%s)
                    """
                }
            }
        }
    }

    post {
        success { echo '✅ settlement-batch 파이프라인 성공' }
        failure { echo '❌ settlement-batch 파이프라인 실패 — 위 콘솔 로그 확인' }
    }
}
