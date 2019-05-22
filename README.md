# junit4-kiuwan-plugin
Integrates junit4 and cobertura reports into Kiuwan.

This plugin (a kiuwan rule) parses junit4/cobertura reports.

The following are the checkers (rules) implemented:
- **JUnit slow test.** Reports a violation when the test execution time exceeds a threshold.
- **JUnit test failed.** Reports a vialation when a JUnit test fails.
- **Cobertura class coverage** Reports a viaolation when a class has coverage rate lower than a specified threshold.

## How to compile
1. From your Kiuwan Local Analyzer installation copy the `{KIUWAN_LOCAL_ANALYZER_INSTALLATION_DIR}/lib.engine/analyzer.jar` to `junit4-kiuwan-plugin/libext/kiuwan-kla-dependencies/analyzer/0.0.0/analyzer-0.0.0.jar`
2. In the junit4-kiuwan-plugin directory, run (the junit tests in this project are dummy test, so skip we them to build the deployment artifact jar file. THey will run later when running Cobertura targets):

	`mvn -DskipTests clean install`
3. **NOTE:** *This step is only necessary if you use this project itself to test.*

	Run maven Cobertura plugin target to generate Junit and cobertura xml report files (TEST-cus.kiuwan.rules.junit.Junit4KiuwanPluginTest.xml and cobertura.xml)

	`mvn cobertura:cobertura`

## How to deploy
1. Copy `junit4-kiuwan-plugin/target/junit4-kiuwan-plugin-x.y.z.jar` to `{KIUWAN_LOCAL_ANALYZER_INSTALLATION_DIR}/lib.custom`
2. Install `junit4-kiuwan-plugin/src/main/resources/ruledef` files in your kiuwan account. See [more on this](https://www.kiuwan.com/docs/display/K5/Installing+rule+definitions+created+with+Kiuwan+Rule+Developer "Install rule definitions") in the Kiuwan documentation. 

## How to run
You can use this project test run the JUnit a Cobertura plugins for Kiuwan
1. Download or clone this repo to a local machine where you have the KLA installed
2. Make sure you have run step 3 in How to compile above
3. Run an analysis on the junit4-kiuwan-plugin drectory using the KLA. More on [how to use the KLA](https://www.kiuwan.com/docs/display/K5/Code+analysis+using+the+downloaded+agent "Kiuwan Local Analyzer") in the Kiuwan documentation.

To run it on your own projects
1. Make sure you checkout the version of your code you want to analyze to the machine you have the KLA installed
2. Make sure you have run JUnit and Cobertura on your project, and that the correspondent xml reports have been generated.
3. Make sure that the model you are going to use in the Kiuwan analysis has the rules you deployed above.
4. Run an analysis on your project using the KLA as described above.

if your Kiuwan analysis is integrated in your CI builds just make sure JUnit and Cobertura run BEFORE the KLA in your pipelines.
