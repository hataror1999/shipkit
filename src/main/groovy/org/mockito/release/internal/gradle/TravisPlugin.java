package org.mockito.release.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.configuration.BasicValidator;
import org.mockito.release.internal.gradle.configuration.LazyConfiguration;

import static org.mockito.release.internal.gradle.GitSetupPlugin.CHECKOUT_BRANCH_TASK;

/**
 * Configures the release automation to be used with Travis CI.
 * <ul>
 * <li>Preconfigures "releasing.build.*" settings based on Travis env variables.</li>
 * <li>Configures {@link GitSetupPlugin#CHECKOUT_BRANCH_TASK} task with value from Travis env variable.</li>
 * </ul>
 */
public class TravisPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GitSetupPlugin.class);
        ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        conf.getBuild().setCommitMessage(System.getenv("TRAVIS_COMMIT_MESSAGE"));

        String buildNo = System.getenv("TRAVIS_BUILD_NUMBER");
        if (buildNo != null) {
            conf.getGit().setCommitMessagePostfix("by Travis CI build " + buildNo + " [ci skip]");
        } else {
            conf.getGit().setCommitMessagePostfix("[ci skip]");
        }

        String pr = System.getenv("TRAVIS_PULL_REQUEST");
        boolean isPullRequest = pr != null && !pr.trim().isEmpty() && !pr.equals("false");
        conf.getBuild().setPullRequest(isPullRequest);

        final GitCheckOutTask checkout = (GitCheckOutTask) project.getTasks().getByName(CHECKOUT_BRANCH_TASK);
        checkout.setRev(System.getenv("TRAVIS_BRANCH"));

        LazyConfiguration.lazyConfiguration(checkout, new Runnable() {
            public void run() {
                BasicValidator.notNull(checkout.getRev(),
                        "Please export 'TRAVIS_BRANCH' environment variable first!\n" +
                                "Alternatively, configure '" + checkout.getPath() + ".rev' task property.");
            }
        });
    }
}