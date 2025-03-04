job('03-Generate-Links') {
    description('generate links for order')
    parameters {
        choiceParam('ORDER_TYPE', ['STANDARD', 'DELUXE', 'PREMIUM'], 'type of order')
        stringParam('TARGET_URL', '', 'target url')
        stringParam('CUSTOMER_NAME', '', 'customer name')
        stringParam('ANCHOR_TEXTS', '', 'anchor texts for link')
        stringParam('KEYWORDS', '', 'crawled keywords')
        stringParam('PROFILE', 'prod', 'active profiles')
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
            
            ANCHOR_TEXTS_JSON=\$(echo "\$ANCHOR_TEXTS" | jq -R 'split(",")' -c)
            KEYWORDS_JSON=\$(echo "\$KEYWORDS" | jq -R 'split(",")' -c)

            ${JAVA_HOME}/bin/java -jar build/libs/*.jar \
            --spring.profiles.active=\$PROFILE \
            --spring.batch.job.name=linkGenerationJob \
            --jasypt.encryptor.password=\$JASYPT_PASSWORD \
            --spring.datasource.url=\$DB_URL \
            --spring.datasource.username=\$DB_USERNAME \
            --spring.datasource.password=\$DB_PASSWORD \
            --spring.datasource.driver-class-name=\$DB_DRIVER_CLASS_NAME \
            orderType=\$ORDER_TYPE \
            targetUrl=\$TARGET_URL \
            customerName=\$CUSTOMER_NAME \
            anchorTexts=\$ANCHOR_TEXTS_JSON \
            keywords="\$KEYWORDS_JSON"
        """)
    }
}