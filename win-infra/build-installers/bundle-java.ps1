function Download-From-Web() {
    $url = $args[0]
    $destination = $args[1]
    $filename = $args[2]
    echo "downloading $url to $destination\$filename"
    (New-Object System.Net.WebClient).DownloadFile($url, "$destination\$filename")
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
function Unzip
{
    $zipfile = $args[0]
    $outpath = $args[1]
    echo "extracting $zipfile to $outpath"
    [System.IO.Compression.ZipFile]::ExtractToDirectory($zipfile, $outpath)
}

$staging_path = "$env:tmp/$java_version"
$java_version = 'jdk1.8.0_101'
$uri = "https://s3-eu-west-1.amazonaws.com/smbc-thirdparty-public-bucket/$java_version/java.zip"
$files_loc = Resolve-Path -Path 'win-infra\files' 

rm -Recurse -Force -ErrorAction Ignore "$files_loc\java"
if (-not (Test-Path "$staging_path\java.zip")) {
    New-Item -Path $staging_path -ErrorAction Ignore -ItemType Directory
    Download-From-Web $uri $staging_path 'java.zip'
}
Unzip "$staging_path\java.zip" "$files_loc\java"
