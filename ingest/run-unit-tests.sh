#!/bin/sh
echo "will now run the midje unit tests; if you supply ':autotest' as a parameter then you'll get a continuous test runner"
./lein clean && ./lein with-profile +unit midje $* 
