job('01-Build-Link-Tree') {
    description('build link tree project')
    steps {
        shell("""
            cd ${LINK_TREE_PRJ_DIR}
            ./gradlew bootJar
        """)
    }
}