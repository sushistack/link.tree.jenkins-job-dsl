job('example-job') {
    description('This is an example job created via Job DSL')
    scm {
        git('https://github.com/your-repo/example.git', 'main')
    }
    triggers {
        scm('H/15 * * * *') // SCM 변경 시 15분마다 폴링
    }
    steps {
        shell('echo "Hello, Jenkins!"')
    }
}