job('crawl-job') {
    description('crwal')
    parameters {
        stringParam('keywords', '', 'keywords for crawl')
    }
    scm {}
    triggers {}
    wrappers {
        credentialsBinding {
            string('JASYPT_PASSWORD', 'jasypt-encryptor-password')
            string('DB_URL', 'datasource-url')
            string('DB_USERNAME', 'datasource-username')
            string('DB_DRIVER_CLASS_NAME', 'datasource-driver-class-name')
        }
    }
    steps {
        shell("""
            echo "Project Dir := [${LINK_TREE_PRJ_DIR}]"
            cd ${LINK_TREE_PRJ_DIR}
            
            keywords_json=$(echo '\$keywords' | jq -R 'split(",")' -c)
            echo "keywords_json := $keywords_json"
            echo "\$JASYPT_PASSWORD"
            echo "\$DB_URL"
            echo "\$DB_USERNAME"
            echo "\$DB_DRIVER_CLASS_NAME"
        """)
    }
}