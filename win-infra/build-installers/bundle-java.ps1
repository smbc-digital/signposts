function Download-From-Web() {
    $url = $args[0]
    $destination = $args[1] 
    Start-BitsTransfer -Source $url -Destination $destination
}

$java_version = 'jdk1.8.0_101'
echo $uri
$uri = "https://s3-eu-west-1.amazonaws.com/smbc-thirdparty-public-bucket/$java_version/java.zip"
# should be $1
$files_loc = 'win-infra\files'

rm -Recurse -Force -ErrorAction Ignore  "$files_loc\java"
Download-From-Web $uri "$files_loc\java.zip"
Expand-Archive "$files_loc\java.zip" -DestinationPath $files_loc\java
rm "$files_loc\java.zip"