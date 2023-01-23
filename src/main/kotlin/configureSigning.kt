import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension

/**
 *     GPG_PRIVATE_KEY should contain the armoured private key that starts with -----BEGIN PGP PRIVATE KEY BLOCK-----
 *     It can be obtained with gpg --armour --export-secret-keys KEY_ID
 */
fun Project.configureSigning(
    privateKey: String,
    password: String
) = configure<SigningExtension> {
    useInMemoryPgpKeys(privateKey, password)
    val publicationsContainer = (extensions["publishing"] as PublishingExtension).publications
    sign(publicationsContainer)
}