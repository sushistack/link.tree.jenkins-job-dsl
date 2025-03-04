job('02-Crawl') {
    description('crwal')
    parameters {
        stringParam('KEYWORDS', '', 'crawled keywords')
    }
    steps {
        shell("""
            KEYWORDS_JSON=\$(echo "\$KEYWORDS" | jq -R 'split(",")' -c)

            ${NODE_PATH}/bin/node ${LINK_TREE_CRAWLER_PRJ_DIR}/index.js --appHomeDir ${APP_HOME_DIR} --keywords "\$KEYWORDS_JSON"
        """)
    }
}