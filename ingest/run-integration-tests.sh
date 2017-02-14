#!/bin/sh
echo "will now run the midje integration tests; if you supply ':autotest' as a parameter then you'll get a continuous test runner"
lein clean && lein with-profile +integration midje $* 
