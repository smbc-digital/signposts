function Download-From-Web() {
    $url = $args[0]
    $destination = $args[1]
    $filename = $args[2]
    echo "$destination\$filename"
    (New-Object System.Net.WebClient).DownloadFile($url, "$destination\$filename")
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
function Unzip
{
    $zipfile = $args[0]
    $outpath = $args[1]
    echo $zipfile
    echo $outpath

    [System.IO.Compression.ZipFile]::ExtractToDirectory($zipfile, $outpath)
}


$java_version = 'jdk1.8.0_101'
$uri = "https://s3-eu-west-1.amazonaws.com/smbc-thirdparty-public-bucket/$java_version/java.zip"
# should be $1
$files_loc = Resolve-Path 'win-infra\files'
mkdir -ErrorAction Ignore $files_loc

rm -Recurse -Force -ErrorAction Ignore  "$files_loc\java"
Download-From-Web $uri $files_loc 'java.zip'
Unzip "$files_loc\java.zip" "$files_loc\java"
rm "$files_loc\java.zip"