job('05-Validate-Links') {
    description('validate links for order')
    parameters {
        stringParam('ORDER_SEQ', '', 'orderSeq')
    }
    wrappers {
        credentialsBinding {
            string('JASYPT_PASSWORD', 'jasypt-encryptor-password')
            string('DB_URL', 'datasource-url')
            string('DB_USERNAME', 'datasource-username')
            string('DB_PASSWORD', 'datasource-password')
            string('DB_DRIVER_CLASS_NAME', 'datasource-driver-class-name')
        }
    }
    steps {
        shell("""
            cd ${LINK_TREE_PRJ_DIR}
            TIMESTAMP=\$(date +"%Y%m%d%H%M%S")

            ${JAVA_HOME}/bin/java -jar build/libs/*.jar \
            --spring.profiles.active=prod \
            --spring.batch.job.name=linkValidationJob \
            --jasypt.encryptor.password=\$JASYPT_PASSWORD \
            --spring.datasource.url=\$DB_URL \
            --spring.datasource.username=\$DB_USERNAME \
            --spring.datasource.password=\$DB_PASSWORD \
            --spring.datasource.driver-class-name=\$DB_DRIVER_CLASS_NAME \
            orderSeq="\$ORDER_SEQ"
        """)
    }
}