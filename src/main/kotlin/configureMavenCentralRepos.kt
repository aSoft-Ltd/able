import org.gradle.api.artifacts.dsl.RepositoryHandler

fun RepositoryHandler.configureMavenCentralRepos(username: String, password: String) = maven {
    name = "ossStaging"
    setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
    credentials {
        this.username = username
        this.password = password
    }
}