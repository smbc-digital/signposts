docker build . --build-arg HTTP_PROXY=http://10.0.2.2:3128 --build-arg HTTPS_PROXY=http://10.0.2.2:3128 --build-arg NO_PROXY=127.0.0.1,localhost