job('01-Build-Link-Tree') {
    description('build link tree project')
    scm {}
    triggers {}
    steps {
        shell("""
            echo "Project Dir := [${LINK_TREE_PRJ_DIR}]"
            cd ${LINK_TREE_PRJ_DIR}
            ./gradlew bootJar
        """)
    }
}