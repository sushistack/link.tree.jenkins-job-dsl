job('crawl-job') {
    description('crwal')
    parameters {
        stringParam('KEYWORDS', '', 'keywords for crawl')
    }
    scm {}
    triggers {}
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
            echo "Project Dir := [${LINK_TREE_PRJ_DIR}]"
            cd ${LINK_TREE_PRJ_DIR}
            
            KEYWORDS_JSON=\$(echo "\$KEYWORDS" | jq -R 'split(",")' -c)
            echo "keywords_json := \$KEYWORDS_JSON"
            echo "\$JASYPT_PASSWORD"
            echo "\$DB_URL"
            echo "\$DB_USERNAME"
            echo "\$DB_DRIVER_CLASS_NAME"

            JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home/bin/java \
            -jar build/libs/*.jar \
            --spring.batch.job.name=crawlJob \
            --jasypt.encryptor.password=\$JASYPT_PASSWORD \
            --spring.datasource.url=\$DB_URL \
            --spring.datasource.username=\$DB_USERNAME \
            --spring.datasource.password=\$DB_PASSWORD \
            --spring.datasource.driver-class-name=\$DB_DRIVER_CLASS_NAME \
            keywords=\$KEYWORDS_JSON
        """)
    }
}