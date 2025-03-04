job('02-Crawl') {
    description('crwal')
    parameters {
        stringParam('KEYWORDS', '', 'crawled keywords')
    }
    steps {
        shell("""
            KEYWORDS_JSON=\$(printf "%s" "\$KEYWORDS" | jq -R 'split(",")' -c)

            ${NODE_PATH}/bin/node ${LINK_TREE_CRAWLER_PRJ_DIR}/index.js --appHomeDir ${APP_HOME_DIR} --keywords \$KEYWORDS_JSON
        """)
    }
}