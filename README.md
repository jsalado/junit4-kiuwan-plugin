# junit4-kiuwan-plugin
Integrates junit4 reports into Kiuwan.

This plugin (an kiuwan rule) parses a junit report.

The following are the checkers:
1. **JUnit slow test.** Generates violation when the test execution time exceeds a threshold.
1. **JUnit test failed.** A JUnit test has failed.
