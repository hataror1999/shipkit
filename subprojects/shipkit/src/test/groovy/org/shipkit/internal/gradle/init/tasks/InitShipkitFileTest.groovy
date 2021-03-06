package org.shipkit.internal.gradle.init.tasks

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class InitShipkitFileTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "does not modify shipkit file if it already exists"() {
        given:
        def shipkitFile = tmp.newFile()
        shipkitFile << "foo"

        when:
        InitShipkitFile.initShipkitFile(shipkitFile, "shipkit.gradle", "mockito/mockito")

        then:
        shipkitFile.text == "foo"
    }

    def "creates default shipkit file if it does not exist"() {
        given:
        def shipkitFile = tmp.newFile()

        when:
        InitShipkitFile.createShipkitFile(shipkitFile, "mockito/mockito")

        then:
        shipkitFile.text ==
            """//This default Shipkit configuration file was created automatically and is intended to be checked-in.
//Default configuration is sufficient for local testing and trying out Shipkit.
//To leverage Shipkit fully, please fix the TODO items, refer to our Getting Started Guide for help:
//
//     https://github.com/mockito/shipkit/blob/master/docs/getting-started.md
//
shipkit {
   //TODO is the repository correct?
   gitHub.repository = "mockito/mockito"

   //TODO generate and use your own read-only GitHub personal access token
   gitHub.readOnlyAuthToken = "76826c9ec886612f504d12fd4268b16721c4f85d"

   //TODO generate GitHub write token, and ensure your Travis CI has this env variable exported
   gitHub.writeAuthToken = System.getenv("GH_WRITE_TOKEN")
}

allprojects {
   plugins.withId("com.jfrog.bintray") {

       //Bintray configuration is handled by JFrog Bintray Gradle Plugin
       //For reference see the official documentation: https://github.com/bintray/gradle-bintray-plugin
       bintray {

           //TODO sign up for free open source account with https://bintray.com, then look up your API key on your profile page in Bintray
           key = '7ea297848ca948adb7d3ee92a83292112d7ae989'
           //TODO don't check in the key, remove above line and use env variable exported on CI:
           //key = System.getenv("BINTRAY_API_KEY")

           pkg {
               //TODO configure Bintray settings per your project (https://github.com/bintray/gradle-bintray-plugin)
               repo = 'bootstrap'
               user = 'shipkit-bootstrap-bot'
               userOrg = 'shipkit-bootstrap'
               name = 'maven'
               licenses = ['MIT']
               labels = ['continuous delivery', 'release automation', 'shipkit']
           }
       }
   }
}
"""
    }
}
