# junit4-kiuwan-plugin
Integrates junit4 reports into Kiuwan.

This plugin (a kiuwan rule) parses a junit report.

The following are the checkers (rules) implemented:
1. **JUnit slow test.** Generates violation when the test execution time exceeds a threshold.
1. **JUnit test failed.** A JUnit test has failed.

## how to compile.
1. From your Kiuwan Local Analyzer installation copy the {KIUWAN_LOCAL_ANALYZER_INSTALLATION_DIR}/lib.engine/analyzer.jar in junit4-kiuwan-plugin/libext/kiuwan-kla-dependencies/analyzer/0.0.0/analyzer-0.0.0.jar
1. At junit4-kiuwan-plugin directory, run (the junit tests in this project are dummy test, so skip them): 
	mvn clean install -DskipTests=true

## how to deploy.
1. copy junit4-kiuwan-plugin/target/junit4-kiuwan-plugin-x.y.z.jar to {KIUWAN_LOCAL_ANALYZER_INSTALLATION_DIR}/lib.custom
1. install junit4-kiuwan-plugin/src/main/resources/ruledef files in your kiuwan account. See more on this at https://www.kiuwan.com/docs/display/K5/Installing+rule+definitions+created+with+Kiuwan+Rule+Developer

